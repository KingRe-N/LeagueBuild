package home.data;

public class Champion {
    private String name;
    // top, jungle, mid, adc, supp
    private String lane;
    // tank, bruiser, assassin, carry, caster
    private String role;
    // p(ap), d(ad), l(low damage)
    private String attribute;
    // heavy cc?
    private int crowdControl;
    // current winrate
    private double winRate;

    public Champion(String name, String attribute, int crowdControl){
        this.name = name;
        this.attribute = attribute;
        this.crowdControl = crowdControl;
    }

    public void setWinRate(double winRate, String role) {
        this.winRate = winRate;
        this.role = role;
    }

    public double getWinRate() {
        return winRate;
    }
    public String getRole(){
        return role;
    }
    public int getCrowdControl(){
        return crowdControl;
    }
    public void setLane(String s){
        lane = s;
    }

    public void setAttribute(String s){
        attribute = s;
    }

}
