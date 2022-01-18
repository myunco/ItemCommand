package ml.mcos.itemcommand.item;

import ml.mcos.itemcommand.action.Action;

import java.util.List;

public class Item {
    private String name;
    private List<String> lore;
    private String type;
    private Action[] action;
    private double price;
    private int points;
    private int levels;
    private String permission;
    private int requiredAmount;
    private int cooldown;

    public Item(String name, List<String> lore, String type, Action[] action, double price, int points, int levels, String permission, int requiredAmount, int cooldown) {
        this.name = name;
        this.lore = lore;
        this.type = type;
        this.action = action;
        this.price = price;
        this.points = points;
        this.levels = levels;
        this.permission = permission;
        this.requiredAmount = requiredAmount;
        this.cooldown = cooldown;

    }


}
