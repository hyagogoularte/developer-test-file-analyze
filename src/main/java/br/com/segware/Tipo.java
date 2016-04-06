package br.com.segware;

public enum Tipo {

    ALARME("ALARME"),
    ARME("ARME"),
    DESARME("DESARME"),
    TESTE("TESTE");

    public String value;

    Tipo(String valuee) {
        valuee = this.value;
    }

    public String getValuee(){
        return value;
    }

    public static Tipo getValue(String key) {
        if(key.equals(Tipo.ALARME.name())) {
            return Tipo.ALARME;
        }

        if(key.equals(Tipo.ARME.name())) {
            return Tipo.ARME;
        }

        if(key.equals(Tipo.DESARME.name())) {
            return Tipo.DESARME;
        }

        if(key.equals(Tipo.TESTE.name())) {
            return Tipo.TESTE;
        }

        return null;
    }
}
