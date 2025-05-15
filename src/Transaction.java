import java.security.*;
import java.util.ArrayList;

public class Transaction {
    public String transactionId;
    public PublicKey sender;
    public PublicKey reciepient;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }
    private String calculateHash(){
        sequence++;
        return StringUtil.applySha256(
            StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value) + sequence
    );
    }
    public void generateSignature(PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey,data);

    }
    public boolean verifySignature(){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data,signature);
    }
    public boolean processTransaction(){
        if(verifySignature() == false){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }
        for(TransactionInput i : inputs){
            i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);

        }
        if(getInputsValue() < NoobChain.minimumTransaction){
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.reciepient, value, transactionId));
        outputs.add(new TransactionOutput(this.sender,leftOver, transactionId));

        for(TransactionOutput o : outputs){
            NoobChain.UTXOs.put(o.id, o);
        }
        for(TransactionInput i: inputs){
            if(i.UTXO == null) continue;
            NoobChain.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }
    public float getInputsValue(){
        float total = 0;
        for(TransactionInput i : inputs){
            if(i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }
    public float getOutputValue(){
        float total = 0;
        for(TransactionOutput o : outputs){
            total += o.value;

        }
        return total;
    }

}
