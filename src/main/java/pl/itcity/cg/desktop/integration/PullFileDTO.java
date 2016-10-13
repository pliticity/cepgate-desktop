package pl.itcity.cg.desktop.integration;

public class PullFileDTO {

    public PullFileDTO(String dicId, String symbol, String fileId) {
        this.dicId = dicId;
        this.symbol = symbol;
        this.fileId = fileId;
    }

    public PullFileDTO() {
    }

    private String dicId;

    private String symbol;

    private String fileId;

    public String getDicId() {
        return dicId;
    }

    public void setDicId(String dicId) {
        this.dicId = dicId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
