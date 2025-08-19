package tobyspring.splearn.application.member.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import tobyspring.splearn.application.member.MemberModifyService;
import tobyspring.splearn.application.member.MemberQueryService;
import tobyspring.splearn.application.member.required.EmailSender;
import tobyspring.splearn.application.member.required.MemberRepository;
import tobyspring.splearn.domain.member.Member;
import tobyspring.splearn.domain.member.MemberFixture;
import tobyspring.splearn.domain.member.MemberStatus;
import tobyspring.splearn.domain.shared.Email;

class MemberRegisterManualTest {

    MemberRepository memberRepository;
    MemberFinder memberFinder;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        // save() 호출될 때 member의 ID를 1L로 만들기
        when(memberRepository.save(any(Member.class))).thenAnswer(i -> {
            Member member = i.getArgument(0);
            ReflectionTestUtils.setField(member, "id", 1L);
            return member;
        });

        memberFinder = new MemberQueryService(memberRepository);
    }

    @Test
    void registerTestStub() {
        MemberRegister memberRegister = new MemberModifyService(
                memberFinder,
                memberRepository,
                new EmailSenderStub(),
                MemberFixture.createPasswordEncoder()
        );

        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void registerTestMock() {
        EmailSenderMock emailSenderMock = new EmailSenderMock();
        MemberRegister memberRegister = new MemberModifyService(
                memberFinder,
                memberRepository,
                emailSenderMock,
                MemberFixture.createPasswordEncoder()
        );

        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);

        assertThat(emailSenderMock.getTos()).hasSize(1);
        assertThat(emailSenderMock.getTos().getFirst()).isEqualTo(member.getEmail());
    }

    @Test
    void registerTestMockito() {
        EmailSender emailSenderMock = mock(EmailSender.class);

        MemberRegister memberRegister = new MemberModifyService(
                memberFinder,
                memberRepository,
                emailSenderMock,
                MemberFixture.createPasswordEncoder()
        );

        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);

        verify(emailSenderMock).send(eq(member.getEmail()), anyString(), anyString());
    }

    static class EmailSenderStub implements EmailSender {
        @Override
        public void send(Email email, String subject, String body) {
        }
    }

    static class EmailSenderMock implements EmailSender {
        List<Email> tos = new ArrayList<>();

        @Override
        public void send(Email email, String subject, String body) {
            tos.add(email);
        }

        public List<Email> getTos() {
            return tos;
        }
    }
}
