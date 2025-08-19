package tobyspring.splearn.application.member.required;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import tobyspring.splearn.domain.shared.Email;
import tobyspring.splearn.domain.member.Member;

/**
 * 회원 정보를 저장하거나 조회한다
 */
public interface MemberRepository extends Repository<Member, Long> {
    Member save(Member member);

    Optional<Member> findByEmail(Email email);

    Optional<Member> findById(Long memberId);
}
