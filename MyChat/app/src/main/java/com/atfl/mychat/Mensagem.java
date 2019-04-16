package com.atfl.mychat;

public class Mensagem {

    private String ip;
    private String alias;
    private String mensagem;
    private String dataHora;


    public Mensagem() {
    }

    public Mensagem(String ip, String alias, String mensagem, String dataHora) {
        this.ip = ip;
        this.alias = alias;
        this.mensagem = mensagem;
        this.dataHora = dataHora;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return "Mensagem{" +
                "ip='" + ip + '\'' +
                ", alias='" + alias + '\'' +
                ", mensagem='" + mensagem + '\'' +
                ", dataHora='" + dataHora + '\'' +
                '}';
    }
}
