/**
 * @author Raquel Facchini Batista Franco 
 * @author Rafael Lima Honorato
 * @author Raphael Santos
 * @apiNote Trabalho de Linguagem de Programação Java
 * @version 1.0  
 * Fatec Praia Grande 
 * @since 15 de Junho de 2022.
 */
package vacinometro;

import java.io.Serializable;
import java.util.Objects;

public class TipoDose implements Serializable{
	
	public TipoDose() {
		super();
	}
	
	public TipoDose(String nome) {
		super();
		this.nome = nome;
	}

	private String nome;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoDose other = (TipoDose) obj;
		return Objects.equals(nome, other.nome);
	}
	
	
}
