package tobyspring.splearn.adapter.webapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static tobyspring.splearn.AssertThatUtils.equalsTo;
import static tobyspring.splearn.AssertThatUtils.notNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
import tobyspring.splearn.adapter.webapi.dto.MemberRegisterResponse;
import tobyspring.splearn.application.member.provided.MemberRegister;
import tobyspring.splearn.application.member.required.MemberRepository;
import tobyspring.splearn.domain.member.Member;
import tobyspring.splearn.domain.member.MemberFixture;
import tobyspring.splearn.domain.member.MemberRegisterRequest;
import tobyspring.splearn.domain.member.MemberStatus;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
class MemberApiTest {
    final MockMvcTester mvcTester;
    final ObjectMapper objectMapper;
    final MemberRegister memberRegister;
    final MemberRepository memberRepository;

    @Test
    void register() throws JsonProcessingException, UnsupportedEncodingException {
        MemberRegisterRequest registerRequest = MemberFixture.createMemberRegisterRequest();
        String content = objectMapper.writeValueAsString(registerRequest);

        MvcTestResult result = mvcTester.post().uri("/api/members").contentType(MediaType.APPLICATION_JSON)
                .content(content).exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.memberId", notNull())
                .hasPathSatisfying("$.email", equalsTo(registerRequest.email()));

        MemberRegisterResponse response =
                objectMapper.readValue(result.getResponse().getContentAsString(), MemberRegisterResponse.class);
        Member member = memberRepository.findById(response.memberId()).orElseThrow();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
        assertThat(member.getNickname()).isEqualTo(registerRequest.nickname());
        assertThat(member.getEmail().address()).isEqualTo(registerRequest.email());
    }

    @Test
    void duplicateEmail() throws JsonProcessingException {
        MemberRegisterRequest registerRequest = MemberFixture.createMemberRegisterRequest();
        memberRegister.register(registerRequest);

        String content = objectMapper.writeValueAsString(registerRequest);

        MvcTestResult result = mvcTester.post().uri("/api/members").contentType(MediaType.APPLICATION_JSON)
                .content(content).exchange();

        assertThat(result)
                .apply(print()) // 실패할때만 보이는 로그를 성공/실패 관계 없이 출력하도록 설정
                .hasStatus(HttpStatus.CONFLICT);
    }
}
