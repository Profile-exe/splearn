package tobyspring.splearn.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.member.DuplicateEmailException;
import tobyspring.splearn.domain.member.Member;
import tobyspring.splearn.domain.MemberFixture;
import tobyspring.splearn.domain.member.MemberRegisterRequest;
import tobyspring.splearn.domain.member.MemberStatus;

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration.class)
record MemberRegisterTest(MemberRegister memberRegister, EntityManager entityManager) {
    @Test
    void register() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        System.out.println("member = " + member);

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void duplicateEmailFail() {
        MemberRegisterRequest memberRegisterRequest = MemberFixture.createMemberRegisterRequest();
        memberRegister.register(memberRegisterRequest);

        assertThatThrownBy(() -> memberRegister.register(memberRegisterRequest))
                .isInstanceOf(DuplicateEmailException.class);
    }
    
    @Test
    void activate() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();  // IDENTITY 채번 전략으로 MySQL에서는 flush 없이도 insert 쿼리가 나감 (save 호출 시 persist 에서 insert 쿼리 나감)
        entityManager.clear();

        member = memberRegister.activate(member.getId());
        entityManager.flush();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    void memberRegisterRequestFail() {
        checkValidation(new MemberRegisterRequest("invalid email", "DDING", "longsecret"));
        checkValidation(new MemberRegisterRequest("test@example.com", "o", "longsecret"));
        checkValidation(new MemberRegisterRequest("test@example.com", "DDING", "short"));
    }

    private void checkValidation(MemberRegisterRequest invalidRequest) {
        assertThatThrownBy(() -> memberRegister.register(invalidRequest))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
