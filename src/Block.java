//work of day 1
import java.util.ArrayList;
import java.util.Date;

public class Block{
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    public long timeStamp;
//    private String data;
    private int nonce;

    public Block( String previousHash){

        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }
    public String calculateHash(){
        String calculatedhash = StringUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot );
        return calculatedhash;
    }
    public void mineBlock(int difficulty){
        merkleRoot = StringUtil.getMerkleRoot((transactions));
        String target = StringUtil.getDifficultyString(difficulty);

        while(!hash.substring(0,difficulty).equals(target)){
            nonce++;
            hash  = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    public boolean addTransaction(Transaction transaction){
        if(transaction == null)
            return false;
        if((previousHash != "0")){
            if((transaction.processTransaction() != true)){
                System.out.println("Transaction failed to process. Discarded");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

}