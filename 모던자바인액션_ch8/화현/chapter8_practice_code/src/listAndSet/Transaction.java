package listAndSet;

public class Transaction {
    private String referenceCode;

    public Transaction(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    @Override
    public String toString() {
        return "Transaction{referenceCode='" + referenceCode + "'}";
    }
}
