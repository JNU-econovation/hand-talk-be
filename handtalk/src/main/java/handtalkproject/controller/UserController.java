package handtalkproject.controller;

import handtalkproject.domain.dto.UserSignInDto;
import handtalkproject.domain.dto.UserSignUpDto;
import handtalkproject.domain.entity.User;
import handtalkproject.exception.KeyNotMatchedException;
import handtalkproject.service.EmailService;
import handtalkproject.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/users")
@Api(value = "사용자와 관련된 기능을 수행하는 컨트롤러")
@Slf4j
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @ApiOperation(value = "입력된 이메일로 인증번호를 보냄")
    @ApiImplicitParam(name = "email", value = "이메일 인증 번호를 보낼 이메일 주소")
    @PostMapping("/email-auth")
    public void askEmailAuthKey(String email) throws MessagingException, UnsupportedEncodingException {
        emailService.sendSimpleMessage(email, "손말잇기 회원가입 인증코드입니다.");
    }

    @ApiOperation(value = "이메일로 발송된 인증번호와 사용자가 입력한 인증번호 일치여부 확인")
    @ApiImplicitParam(name = "emailAuthKey", value = "사용자가 입력한 이메일 인증 번호")
    @GetMapping("/email-auth")
    public boolean comepareEmailAuthKeywithInputKey(String emailAuthKey) {
        return emailService.isAuthorized(emailAuthKey);
    }

    @ApiOperation(value = "회원가입", notes = "최종적인 회원가입 요청")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userSignUpDto", value = "이메일, 패스워드, 닉네임, 프로필, 이메일 인증 여부", required = true),
    })
    @PostMapping("/signup")
    public User register(UserSignUpDto userSignUpDto) throws KeyNotMatchedException {
        if (userSignUpDto.isEmailAuthorized()) {
            return userService.save(userSignUpDto.toEntity()); // 이메일 인증 성공했으므로 emailAuthorized 값 true로 변경하여 User 객체로 반환
        }
        throw new KeyNotMatchedException();
    }

    @ApiOperation(value = "로그인", notes = "로그인 요청")
    @ApiImplicitParam(name = "userSignInDto", value = "이메일, 패스워드 입력 값", required = true)
    @PostMapping("/login")
    public User login(UserSignInDto userSignInDto) {
        return userService.login(userSignInDto.toEntity());
    }
}