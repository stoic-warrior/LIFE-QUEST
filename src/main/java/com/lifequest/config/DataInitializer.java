package com.lifequest.config;

import com.lifequest.domain.dungeon.Dungeon;
import com.lifequest.domain.dungeon.DungeonRepository;
import com.lifequest.domain.dungeon.EffectType;
import com.lifequest.domain.dungeon.Environment;
import com.lifequest.domain.item.Item;
import com.lifequest.domain.item.ItemGrade;
import com.lifequest.domain.item.ItemRepository;
import com.lifequest.domain.item.ItemSlot;
import com.lifequest.domain.monster.Monster;
import com.lifequest.domain.monster.MonsterRepository;
import com.lifequest.domain.monster.Trait;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MonsterRepository monsterRepository;
    private final DungeonRepository dungeonRepository;
    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) {
        if (monsterRepository.count() == 0) {
            initMonsters();
            initDungeons();
            initItems();
        }
    }

    private void initMonsters() {
        monsterRepository.save(Monster.create("슬라임", 100, 5, 12, 50, 20, Trait.NONE, 0, 0.3));
        monsterRepository.save(Monster.create("고블린", 300, 10, 6, 150, 60, Trait.CRIT_IMMUNE, 0, 0.25));
        monsterRepository.save(Monster.create("골렘", 500, 15, 8, 300, 120, Trait.ARMOR, 0.2, 0.2));
        monsterRepository.save(Monster.create("뱀파이어", 400, 20, 4, 400, 150, Trait.LIFESTEAL, 0.3, 0.2));
        monsterRepository.save(Monster.create("드래곤", 1000, 30, 2, 1000, 500, Trait.ACCELERATE, 0.1, 0.15));
    }

    private void initDungeons() {
        Monster slime = monsterRepository.findById(1L).orElseThrow();
        Monster goblin = monsterRepository.findById(2L).orElseThrow();
        Monster golem = monsterRepository.findById(3L).orElseThrow();
        Monster vampire = monsterRepository.findById(4L).orElseThrow();
        Monster dragon = monsterRepository.findById(5L).orElseThrow();

        // 초원
        dungeonRepository.save(Dungeon.create("초원 1층", 1, Environment.PLAIN, "효과 없음", EffectType.NONE, 0, slime, 1));
        dungeonRepository.save(Dungeon.create("초원 2층", 2, Environment.PLAIN, "효과 없음", EffectType.NONE, 0, goblin, 3));

        // 늪지
        dungeonRepository.save(Dungeon.create("늪지 1층", 3, Environment.SWAMP, "공격력 -10%", EffectType.ATK_DEBUFF, 0.1, goblin, 5));
        dungeonRepository.save(Dungeon.create("늪지 2층", 4, Environment.SWAMP, "공격력 -10%", EffectType.ATK_DEBUFF, 0.1, vampire, 7));

        // 화산
        dungeonRepository.save(Dungeon.create("화산 1층", 5, Environment.VOLCANO, "시간당 HP -1", EffectType.DOT_DAMAGE, 1, golem, 10));
        dungeonRepository.save(Dungeon.create("화산 2층", 6, Environment.VOLCANO, "시간당 HP -2", EffectType.DOT_DAMAGE, 2, dragon, 15));

        // 빙하
        dungeonRepository.save(Dungeon.create("빙하 1층", 7, Environment.GLACIER, "몬스터 공격주기 +50%", EffectType.SLOW_MONSTER, 0.5, golem, 12));
        dungeonRepository.save(Dungeon.create("빙하 2층", 8, Environment.GLACIER, "몬스터 공격주기 +50%", EffectType.SLOW_MONSTER, 0.5, dragon, 20));
    }

    private void initItems() {
        // 무기
        itemRepository.save(Item.create("나무 검", "기본 검", ItemGrade.COMMON, ItemSlot.WEAPON, 5, 0, 0, 0, 50));
        itemRepository.save(Item.create("철 검", "단단한 검", ItemGrade.UNCOMMON, ItemSlot.WEAPON, 12, 0, 2, 0, 200));
        itemRepository.save(Item.create("불꽃 검", "화염이 깃든 검", ItemGrade.RARE, ItemSlot.WEAPON, 25, 0, 5, 0, 500));
        itemRepository.save(Item.create("암흑 검", "어둠의 힘", ItemGrade.EPIC, ItemSlot.WEAPON, 40, 0, 10, 5, 1500));
        itemRepository.save(Item.create("전설의 검", "전설의 무기", ItemGrade.LEGENDARY, ItemSlot.WEAPON, 60, 10, 15, 10, 5000));

        // 방어구
        itemRepository.save(Item.create("천 옷", "기본 옷", ItemGrade.COMMON, ItemSlot.ARMOR, 0, 5, 0, 0, 50));
        itemRepository.save(Item.create("가죽 갑옷", "가벼운 갑옷", ItemGrade.UNCOMMON, ItemSlot.ARMOR, 0, 12, 0, 2, 200));
        itemRepository.save(Item.create("철 갑옷", "단단한 갑옷", ItemGrade.RARE, ItemSlot.ARMOR, 0, 25, 0, 5, 500));
        itemRepository.save(Item.create("미스릴 갑옷", "가볍고 단단함", ItemGrade.EPIC, ItemSlot.ARMOR, 5, 40, 5, 5, 1500));
        itemRepository.save(Item.create("드래곤 갑옷", "용의 비늘", ItemGrade.LEGENDARY, ItemSlot.ARMOR, 10, 60, 10, 15, 5000));

        // 악세서리
        itemRepository.save(Item.create("구리 반지", "기본 반지", ItemGrade.COMMON, ItemSlot.ACCESSORY, 2, 2, 2, 2, 50));
        itemRepository.save(Item.create("은 반지", "빛나는 반지", ItemGrade.UNCOMMON, ItemSlot.ACCESSORY, 5, 5, 5, 5, 200));
        itemRepository.save(Item.create("금 반지", "고급 반지", ItemGrade.RARE, ItemSlot.ACCESSORY, 8, 8, 8, 8, 500));
        itemRepository.save(Item.create("마법 반지", "마력이 깃든 반지", ItemGrade.EPIC, ItemSlot.ACCESSORY, 12, 12, 12, 12, 1500));
        itemRepository.save(Item.create("전설의 반지", "전설의 장신구", ItemGrade.LEGENDARY, ItemSlot.ACCESSORY, 20, 20, 20, 20, 5000));
    }
}
