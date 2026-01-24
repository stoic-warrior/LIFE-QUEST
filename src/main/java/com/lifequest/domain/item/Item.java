package com.lifequest.domain.item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemGrade grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemSlot slot;

    @Column(nullable = false)
    private int atkBonus = 0;

    @Column(nullable = false)
    private int defBonus = 0;

    @Column(nullable = false)
    private int crtBonus = 0;

    @Column(nullable = false)
    private int lukBonus = 0;

    @Column(nullable = false)
    private int price = 0;

    private String imageUrl;

    public static Item create(String name, String description, ItemGrade grade, ItemSlot slot,
                               int atkBonus, int defBonus, int crtBonus, int lukBonus, int price) {
        Item item = new Item();
        item.name = name;
        item.description = description;
        item.grade = grade;
        item.slot = slot;
        item.atkBonus = atkBonus;
        item.defBonus = defBonus;
        item.crtBonus = crtBonus;
        item.lukBonus = lukBonus;
        item.price = price;
        return item;
    }
}
