package handtalkproject.controller;

import handtalkproject.domain.dto.UserSignInDto;
import handtalkproject.domain.dto.UserSignUpDto;
import handtalkproject.domain.entity.User;
import handtalkproject.exception.DuplicatedEmailException;
import handtalkproject.exception.KeyNotMatchedException;
import handtalkproject.service.AwsS3Service;
import handtalkproject.service.EmailService;
import handtalkproject.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/users")
@Api(value = "사용자와 관련된 기능을 수행하는 컨트롤러")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    private static final String KEY_NOT_MATCHED_MESSAGE = "이메일 인증번호가 일치하지 않습니다.";

    private final UserService userService;
    private final EmailService emailService;

    private final AwsS3Service awsS3Service;

//    private final HttpSession session;

    @ApiOperation(value = "입력된 이메일로 인증번호를 보냄")
    @ApiImplicitParam(name = "email", value = "이메일 인증 번호를 보낼 이메일 주소")
    @PostMapping("/email-auth")
    public void askEmailAuthKey(@RequestBody String email) throws MessagingException, UnsupportedEncodingException {
        emailService.sendSimpleMessage(email, "손말잇기 회원가입 인증코드입니다.");
    }

    @ApiOperation(value = "이메일로 발송된 인증번호와 사용자가 입력한 인증번호 일치여부 확인")
    @ApiImplicitParam(name = "emailAuthKey", value = "사용자가 입력한 이메일 인증 번호")
    @GetMapping("/email-auth")
    public boolean comepareEmailAuthKeywithInputKey(String emailAuthKey) {
        return emailService.isAuthorized(emailAuthKey);
    }

    @ApiOperation(value = "회원가입", notes = "최종적인 회원가입 요청")
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "profileImageFile", value = "사용자 프로필 사진 파일", type = "MultipartFile")
    } )
    @PostMapping(value = "/signup")
    public User create(UserSignUpDto userSignUpDto, MultipartFile profileImageFile, HttpServletRequest request) throws IOException, ServletException {

        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        System.out.println("메시지 바디 =" + messageBody);

        if (userSignUpDto.isEmailAuthorized()) {
            String imageUrl = awsS3Service.uploadProfile(profileImageFile);
            return userService.save(userSignUpDto.toEntity(imageUrl)); // 이메일 인증 성공했으므로 emailAuthorized 값 true로 변경하여 User 객체로 반환, 이미지 주소 엔티티에 저장
        }
        throw new KeyNotMatchedException(KEY_NOT_MATCHED_MESSAGE);
    }

    @ApiOperation(value = "로그인", notes = "로그인 요청")
    @GetMapping("/login")
    public UserSignInDto login(UserSignInDto userSignInDto) {
        User user = userService.login(userSignInDto.toEntity());
//        session.setAttribute(UserSessionUtils.USER_SESSION_KEY, user);

        return user.toDto();
    }

//    @ApiOperation(value = "로그아웃", notes = "로그아웃 요청")
//    @GetMapping("/logout")
//    public void logout() {
//        session.invalidate();
//    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public String catchDuplicatedEmailException(DuplicatedEmailException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(KeyNotMatchedException.class)
    public String catchKeyNotMatched(KeyNotMatchedException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler({MessagingException.class, UnsupportedEncodingException.class})
    public String catchEmailException(Exception exception) {
        return exception.getMessage();
    }
}