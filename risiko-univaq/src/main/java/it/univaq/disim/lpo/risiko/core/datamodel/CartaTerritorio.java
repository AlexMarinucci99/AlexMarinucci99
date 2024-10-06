package it.univaq.disim.lpo.risiko.core.datamodel;
import java.io.Serializable;

public class CartaTerritorio implements Serializable{
	  private static final long serialVersionUID = 1L;
    private Territorio territorio;
    private String figura;

	public CartaTerritorio(Territorio territorio, String figura) {
        this.territorio = territorio;
        this.figura = figura;
    }

    public Territorio getTerritorio() {
        return this.territorio;
    }

    public void setTerritorio(Territorio territorio) {
        this.territorio = territorio;
    }

    public String getFigura() {
        return this.figura;
    }

    public void setFigura(String figura) {
        this.figura = figura;
    }
}