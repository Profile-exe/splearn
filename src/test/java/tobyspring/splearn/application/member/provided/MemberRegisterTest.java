package tobyspring.splearn.application.member.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tobyspring.splearn.domain.member.MemberFixture.createMemberInfoUpdateRequest;
import static tobyspring.splearn.domain.member.MemberFixture.createMemberRegisterRequest;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.member.DuplicateEmailException;
import tobyspring.splearn.domain.member.DuplicateProfileException;
import tobyspring.splearn.domain.member.Member;
import tobyspring.splearn.domain.member.MemberInfoUpdateRequest;
import tobyspring.splearn.domain.member.MemberRegisterRequest;
import tobyspring.splearn.domain.member.MemberStatus;

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration.class)
record MemberRegisterTest(MemberRegister memberRegister, EntityManager entityManager) {
    @Test
    void register() {
        Member member = memberRegister.register(createMemberRegisterRequest());

        System.out.println("member = " + member);

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void duplicateEmailFail() {
        MemberRegisterRequest memberRegisterRequest = createMemberRegisterRequest();
        memberRegister.register(memberRegisterRequest);

        assertThatThrownBy(() -> memberRegister.register(memberRegisterRequest))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void activate() {
        Member member = registerMember();

        member = memberRegister.activate(member.getId());
        entityManager.flush();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getDetail().getActivatedAt()).isNotNull();
    }

    private Member registerMember() {
        Member member = memberRegister.register(createMemberRegisterRequest());
        entityManager.flush();  // IDENTITY 채번 전략으로 MySQL에서는 flush 없이도 insert 쿼리가 나감 (save 호출 시 persist 에서 insert 쿼리 나감)
        entityManager.clear();
        return member;
    }

    private Member registerMember(String email) {
        Member member = memberRegister.register(createMemberRegisterRequest(email));
        entityManager.flush();  // IDENTITY 채번 전략으로 MySQL에서는 flush 없이도 insert 쿼리가 나감 (save 호출 시 persist 에서 insert 쿼리 나감)
        entityManager.clear();
        return member;
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

    @Test
    void deactivate() {
        Member member = registerMember();

        memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();

        member = memberRegister.deactivate(member.getId());
        entityManager.flush();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
    }

    @Test
    void updateInfo() {
        Member member = registerMember();

        Long memberId = member.getId();
        memberRegister.activate(memberId);
        entityManager.flush();
        entityManager.clear();

        var updateRequest = createMemberInfoUpdateRequest();
        member = memberRegister.updateInfo(memberId, updateRequest);

        assertThat(member.getDetail().getProfile().address()).isEqualTo(updateRequest.profileAddress());

        // 기존 프로필 주소로 계속 변경 요청 가능
        memberRegister.updateInfo(memberId, updateRequest);

        // 다른 프로필 주소로 변경 가능
        memberRegister.updateInfo(memberId, createMemberInfoUpdateRequest("omg123"));

        // 프로필 주소 제거 가능
        memberRegister.updateInfo(memberId, createMemberInfoUpdateRequest(""));
    }

    @Test
    void updateInfoFail() {
        Member member = registerMember();
        Long memberId = member.getId();
        memberRegister.activate(memberId);
        member = memberRegister.updateInfo(memberId, createMemberInfoUpdateRequest());

        Member anotherMember = registerMember("another@email.com");
        Long anotherMemberId = anotherMember.getId();
        memberRegister.activate(anotherMemberId);
        entityManager.flush();
        entityManager.clear();

        // anotherMember가 member와 프로필 주소 중복
        MemberInfoUpdateRequest duplicateProfileUpdateRequest = createMemberInfoUpdateRequest(member.getDetail().getProfile().address());
        assertThatThrownBy(() -> {
            memberRegister.updateInfo(anotherMemberId, duplicateProfileUpdateRequest);
        }).isInstanceOf(DuplicateProfileException.class);

        // member와 중복되지 않는 프로필 주소로는 변경 가능
        MemberInfoUpdateRequest updateRequest = createMemberInfoUpdateRequest("profile123");
        memberRegister.updateInfo(anotherMemberId, updateRequest);

        // member가 anotherMember와 프로필 주소 중복
        assertThatThrownBy(() -> {
            memberRegister.updateInfo(memberId, updateRequest);
        }).isInstanceOf(DuplicateProfileException.class);
    }
}
