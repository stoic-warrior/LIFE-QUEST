package com.lifequest.config;

import com.lifequest.domain.boss.BossSummon;
import com.lifequest.domain.boss.BossSummonRepository;
import com.lifequest.domain.hunting.HuntingGround;
import com.lifequest.domain.hunting.HuntingGroundRepository;
import com.lifequest.domain.item.Item;
import com.lifequest.domain.item.ItemGrade;
import com.lifequest.domain.item.ItemRepository;
import com.lifequest.domain.item.ItemSlot;
import com.lifequest.domain.monster.Monster;
import com.lifequest.domain.monster.MonsterRepository;
import com.lifequest.domain.monster.Trait;
import com.lifequest.domain.user.PlayerStats;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import com.lifequest.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MonsterRepository monsterRepository;
    private final HuntingGroundRepository huntingGroundRepository;
    private final BossSummonRepository bossSummonRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (monsterRepository.count() == 0) {
            initMonsters();
            initBosses();
            initHuntingGrounds();
            initBossSummons();
            initItems();
        }
        
        // 어드민 계정 생성 (없으면)
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            initAdminUser();
        }
    }

    private void initAdminUser() {
        User admin = User.create(
                "admin@admin.com",
                "Admin",
                passwordEncoder.encode("admin"),
                UUID.randomUUID()
        );
        admin.setRole(UserRole.ADMIN);
        admin.setGold(999999);
        admin.setLevel(99);
        admin.setMaxHp(9999);
        admin.setCurrentHp(9999);
        admin.setStatPoints(999);
        
        PlayerStats stats = PlayerStats.create();
        stats.setAtk(100);
        stats.setDef(100);
        stats.setPen(100);
        stats.setLuk(100);
        admin.initializeStats(stats);
        
        userRepository.save(admin);
    }

    private void initMonsters() {
        // 사냥터 몬스터 (ID 1~6)
        // Monster.create(name, hp, atk, def, attackInterval, xp, gold, trait, traitValue, dropRate)
        monsterRepository.save(Monster.create("슬라임", 100, 5, 0, 12, 50, 20, Trait.NONE, 0, 0.3));
        monsterRepository.save(Monster.create("고블린", 200, 10, 10, 8, 100, 40, Trait.RANDOM, 0, 0.25));
        monsterRepository.save(Monster.create("독 슬라임", 250, 8, 5, 8, 120, 50, Trait.POISON, 2, 0.25));
        monsterRepository.save(Monster.create("골렘", 400, 15, 40, 6, 200, 80, Trait.MIRROR, 0.2, 0.2));
        monsterRepository.save(Monster.create("얼음 정령", 350, 18, 15, 6, 250, 100, Trait.REGENERATE, 0.05, 0.2));
        monsterRepository.save(Monster.create("와이번", 500, 25, 25, 4, 400, 160, Trait.NONE, 0, 0.15));
    }

    private void initBosses() {
        // 보스 몬스터 (ID 7~12)
        monsterRepository.save(Monster.createBoss("슬라임 킹", 3000, 50, 10, 4, 2000, 1000, Trait.REGENERATE, 0.03, 1.0, null));
        monsterRepository.save(Monster.createBoss("고블린 킹", 5000, 70, 20, 4, 4000, 2000, Trait.RANDOM, 0, 1.0, null));
        monsterRepository.save(Monster.createBoss("독의 여왕", 6000, 60, 15, 4, 5000, 2500, Trait.POISON, 5, 1.0, null));
        monsterRepository.save(Monster.createBoss("불의 군주", 8000, 100, 45, 3, 7000, 3500, Trait.MIRROR, 0.25, 1.0, null));
        monsterRepository.save(Monster.createBoss("서리 군주", 12000, 130, 50, 3, 10000, 5000, Trait.REGENERATE, 0.05, 1.0, null));
        monsterRepository.save(Monster.createBoss("고대 드래곤", 20000, 200, 35, 2, 20000, 10000, Trait.RANDOM, 0, 1.0, null));
    }

    private void initHuntingGrounds() {
        Monster slime = monsterRepository.findById(1L).orElseThrow();
        Monster goblin = monsterRepository.findById(2L).orElseThrow();
        Monster poisonSlime = monsterRepository.findById(3L).orElseThrow();
        Monster golem = monsterRepository.findById(4L).orElseThrow();
        Monster iceSpirit = monsterRepository.findById(5L).orElseThrow();
        Monster wyvern = monsterRepository.findById(6L).orElseThrow();

        Monster slimeKing = monsterRepository.findById(7L).orElseThrow();
        Monster goblinKing = monsterRepository.findById(8L).orElseThrow();
        Monster poisonQueen = monsterRepository.findById(9L).orElseThrow();
        Monster fireLord = monsterRepository.findById(10L).orElseThrow();
        Monster frostLord = monsterRepository.findById(11L).orElseThrow();

        // HuntingGround.create(name, monster, requiredLevel, unlockBoss, description)
        huntingGroundRepository.save(HuntingGround.create("초원", slime, 1, null, "평화로운 초원. 슬라임이 서식한다."));
        huntingGroundRepository.save(HuntingGround.create("숲", goblin, 3, slimeKing, "울창한 숲. 고블린들이 숨어있다."));
        huntingGroundRepository.save(HuntingGround.create("늪지", poisonSlime, 7, goblinKing, "독이 가득한 늪지."));
        huntingGroundRepository.save(HuntingGround.create("화산", golem, 12, poisonQueen, "뜨거운 화산 지대."));
        huntingGroundRepository.save(HuntingGround.create("빙하", iceSpirit, 18, fireLord, "얼어붙은 빙하."));
        huntingGroundRepository.save(HuntingGround.create("용의 계곡", wyvern, 25, frostLord, "드래곤의 영역."));
    }

    private void initBossSummons() {
        Monster slime = monsterRepository.findById(1L).orElseThrow();
        Monster goblin = monsterRepository.findById(2L).orElseThrow();
        Monster poisonSlime = monsterRepository.findById(3L).orElseThrow();
        Monster golem = monsterRepository.findById(4L).orElseThrow();
        Monster iceSpirit = monsterRepository.findById(5L).orElseThrow();
        Monster wyvern = monsterRepository.findById(6L).orElseThrow();

        Monster slimeKing = monsterRepository.findById(7L).orElseThrow();
        Monster goblinKing = monsterRepository.findById(8L).orElseThrow();
        Monster poisonQueen = monsterRepository.findById(9L).orElseThrow();
        Monster fireLord = monsterRepository.findById(10L).orElseThrow();
        Monster frostLord = monsterRepository.findById(11L).orElseThrow();
        Monster ancientDragon = monsterRepository.findById(12L).orElseThrow();

        // BossSummon.create(name, boss, price, requiredMonster, requiredKillCount, description)
        bossSummonRepository.save(BossSummon.create("슬라임 킹 소환서", slimeKing, 300, slime, 50, "슬라임의 왕을 소환한다."));
        bossSummonRepository.save(BossSummon.create("고블린 킹 소환서", goblinKing, 800, goblin, 100, "고블린의 왕을 소환한다."));
        bossSummonRepository.save(BossSummon.create("독의 여왕 소환서", poisonQueen, 2000, poisonSlime, 100, "독을 다스리는 여왕을 소환한다."));
        bossSummonRepository.save(BossSummon.create("불의 군주 소환서", fireLord, 4000, golem, 150, "화염의 군주를 소환한다."));
        bossSummonRepository.save(BossSummon.create("서리 군주 소환서", frostLord, 7000, iceSpirit, 150, "얼음의 군주를 소환한다."));
        bossSummonRepository.save(BossSummon.create("고대 드래곤 소환서", ancientDragon, 15000, wyvern, 200, "전설의 드래곤을 소환한다."));
    }

    private void initItems() {
        // 무기 (ATK, DEF, PEN, LUK)
        itemRepository.save(Item.create("나무 검", "기본 검", ItemGrade.COMMON, ItemSlot.WEAPON, 5, 0, 0, 0, 100));
        itemRepository.save(Item.create("철 검", "단단한 철검", ItemGrade.UNCOMMON, ItemSlot.WEAPON, 15, 0, 5, 0, 500));
        itemRepository.save(Item.create("불꽃 검", "화염이 깃든 검", ItemGrade.RARE, ItemSlot.WEAPON, 30, 0, 15, 0, 2000));
        itemRepository.save(Item.create("드래곤 슬레이어", "드래곤을 베는 검", ItemGrade.LEGENDARY, ItemSlot.WEAPON, 50, 0, 25, 10, 10000));

        // 방어구
        itemRepository.save(Item.create("천 갑옷", "기본 갑옷", ItemGrade.COMMON, ItemSlot.ARMOR, 0, 5, 0, 0, 100));
        itemRepository.save(Item.create("철 갑옷", "단단한 갑옷", ItemGrade.UNCOMMON, ItemSlot.ARMOR, 0, 15, 0, 0, 500));
        itemRepository.save(Item.create("미스릴 갑옷", "가볍고 튼튼한 갑옷", ItemGrade.RARE, ItemSlot.ARMOR, 5, 30, 0, 0, 2000));
        itemRepository.save(Item.create("드래곤 아머", "드래곤 비늘 갑옷", ItemGrade.LEGENDARY, ItemSlot.ARMOR, 10, 50, 0, 5, 10000));

        // 악세서리
        itemRepository.save(Item.create("행운의 반지", "행운을 가져다주는 반지", ItemGrade.COMMON, ItemSlot.ACCESSORY, 0, 0, 0, 10, 200));
        itemRepository.save(Item.create("관통의 반지", "방어를 무시하는 반지", ItemGrade.UNCOMMON, ItemSlot.ACCESSORY, 0, 0, 15, 0, 800));
        itemRepository.save(Item.create("전사의 목걸이", "전투력을 올려주는 목걸이", ItemGrade.RARE, ItemSlot.ACCESSORY, 20, 10, 10, 5, 3000));
        itemRepository.save(Item.create("드래곤 하트", "드래곤의 심장", ItemGrade.LEGENDARY, ItemSlot.ACCESSORY, 30, 30, 20, 20, 15000));
    }
}
