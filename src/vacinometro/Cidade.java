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

public class Cidade implements Serializable, Comparable<Cidade>{

	private String nome;

	public Cidade(String nomeCidade) {
		this.nome = nomeCidade;
	}

	public Cidade() {
	}

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
		Cidade other = (Cidade) obj;
		return Objects.equals(nome, other.nome);
	}

	@Override
	public int compareTo(Cidade cid) {
		return this.nome.compareTo(cid.getNome());
	}
	
}