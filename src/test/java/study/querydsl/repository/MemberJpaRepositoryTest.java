package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    MemberJpaRepository memberJpaRepository;
    
    @Test
    public void basicTest() {
        // given
        Member member = new Member("member1", 10);

        //when
        memberJpaRepository.save(member);

        //then
        Member findMember = memberJpaRepository.findById(member.getId()).get();
        List<Member> result1 = memberJpaRepository.findAll();
        List<Member> result2 = memberJpaRepository.findByUsername("member1");

        assertThat(findMember).isEqualTo(member);
        assertThat(result1).containsExactly(member);
        assertThat(result2).containsExactly(member);
    }
    
    @Test
    public void basicQuerydslTest() {
        // given
        Member member = new Member("member1", 10);

        //when
        memberJpaRepository.save(member);

        //then
        List<Member> result1 = memberJpaRepository.findAll_Querydsl();
        List<Member> result2 = memberJpaRepository.findByUsername_Querydsl("member1");

        assertThat(result1).containsExactly(member);
        assertThat(result2).containsExactly(member);
    }
    
    @Test
    public void searchTest() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        //when
//        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition);
        List<MemberTeamDto> result = memberJpaRepository.search(condition);

        //then
        assertThat(result).extracting("username").containsExactly("member4");

    }
}