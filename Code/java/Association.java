import java.text.DecimalFormat;


public class Association {

	private int affinity;
	private double moyenne;
	private double somme;
	private int nbAssoci;
	private double sumEcartType;
	private double ecartType;
	
	public Association(){
		this.affinity=0;
		this.ecartType=0;
		this.sumEcartType=0;
		this.moyenne=0;
		this.somme=0;
		this.nbAssoci=0;
	}
	
	public Association(int affinity, double moyenne, double ecartType) {
		this.affinity=affinity;
		this.ecartType=ecartType;
		this.sumEcartType=0;
		this.moyenne=moyenne;
		this.somme=0;
		this.nbAssoci=0;
	}
	
	public int getAffinity(){return this.affinity;}
	
	public void increAffinity(){this.affinity++;}
	
	public void print(){
		this.calculEcartType();
		DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
        System.out.print(this.affinity+"-");
        System.out.printf("%.9f-", this.moyenne);
        //System.out.print(+"-");
        System.out.printf("%.9f", this.ecartType);
        //System.out.print(this.ecartType);
	}

	public boolean isInit(){
		return (this.somme==0 && this.sumEcartType==0);
	}
	
	public void increSomme(double x){
		this.somme+=x;
	}
	
	public void increNbAssoc(){
		this.nbAssoci++;
	}
	
	public double calcMoyenne(double x){
		this.increSomme(x);
		this.increNbAssoc();
		this.moyenne=this.somme/this.nbAssoci;
		return this.moyenne;
	}
	
	public void increSumEcartType(double x){
		this.sumEcartType+=(x-this.moyenne)*(x-this.moyenne);
	}
	
	public void calculEcartType(){
		if(this.nbAssoci!=0){
			this.ecartType=Math.sqrt(this.sumEcartType/this.nbAssoci);
		}
		else this.ecartType=0;
	}
	
	public static Association parseAssociation(String line) {
		String[] subLine = line.split("-");
		int affinity = new Integer(subLine[0]);
		double moyenne = Double.parseDouble(subLine[1].replace(",","."));
		double ecartType = Double.parseDouble(subLine[2].replace(",","."));
		return new Association(affinity, moyenne, ecartType);
	}

	public double getMoyenne() {
		return this.moyenne;
	}

	public double getEcartType() {
		return this.ecartType;
	}
	
}
