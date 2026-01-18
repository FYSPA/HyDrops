package dev.yh.model;

public class LootEntry {
    private String id;
    private int min;
    private int max;
    private int weight;

    // Setters necesarios para que Gson pueda escribir o nosotros crear defaults
    public void setId(String id) { this.id = id; }
    public void setMin(int min) { this.min = min; }
    public void setMax(int max) { this.max = max; }
    public void setWeight(int weight) { this.weight = weight; }

    // Getters
    public String getId() { return id; }
    public int getMin() { return min; }
    public int getMax() { return max; }
    public int getWeight() { return weight; }
}