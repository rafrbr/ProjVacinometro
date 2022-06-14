package vacinometro;

import java.io.Serializable;
import java.util.Objects;

public class DosesAplicadas implements Serializable, Comparable<DosesAplicadas> {
	
	private Cidade cidade;
	private TipoDose tipoDose;
	private int qtdDosesAplicadas;
	
	public Cidade getCidade() {
		return cidade;
	}
	public DosesAplicadas(Cidade cidade, TipoDose tipoDose, int qtdDosesAplicadas) {
		super();
		this.cidade = cidade;
		this.tipoDose = tipoDose;
		this.qtdDosesAplicadas = qtdDosesAplicadas;
	}
	
	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}
	public TipoDose getTipoDose() {
		return tipoDose;
	}
	public void setTipoDose(TipoDose tipoDose) {
		this.tipoDose = tipoDose;
	}
	public int getQtdDosesAplicadas() {
		return qtdDosesAplicadas;
	}
	public void setQtdDosesAplicadas(int qtdDosesAplicadas) {
		this.qtdDosesAplicadas = qtdDosesAplicadas;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(cidade, qtdDosesAplicadas, tipoDose);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DosesAplicadas other = (DosesAplicadas) obj;
		return Objects.equals(cidade, other.cidade) && qtdDosesAplicadas == other.qtdDosesAplicadas
				&& Objects.equals(tipoDose, other.tipoDose);
	}
	@Override
	public int compareTo(DosesAplicadas o) {
		return this.cidade.compareTo(o.cidade);
	}
}