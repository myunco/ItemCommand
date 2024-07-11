package ml.mcos.itemcommand.item;

public enum Trigger {
    LEFT, // 左键点击
    RIGHT, // 右键点击
    SNEAK_LEFT, // 潜行时左键点击
    SNEAK_RIGHT, // 潜行时右键点击
    OFFHAND_LEFT, // 副手左键点击
    OFFHAND_RIGHT, // 副手右键点击
    SNEAK_OFFHAND_LEFT, // 潜行时副手左键点击
    SNEAK_OFFHAND_RIGHT, // 潜行时副手右键点击
    HELD, // 切换至手持
    INV_LEFT, // 物品栏内左键点击
    INV_RIGHT, // 物品栏内右键点击
    INV_SHIFT_LEFT, // 物品栏内Shift+左键点击
    INV_SHIFT_RIGHT, // 物品栏内Shift+右键点击
    HAND_HIT, // 主手手持物品时被攻击
    OFFHAND_HIT, // 副手手持物品时被攻击
    ARMOR_HIT // 装备在身上时被攻击
}
