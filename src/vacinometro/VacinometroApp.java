package vacinometro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class VacinometroApp implements Serializable {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		Locale.setDefault(Locale.ENGLISH);
		int op = -1;
		do {
			try {
				System.out.println("\nVacinômetro");
				System.out.println("<1> Cadastrar cidade");
				System.out.println("<2> Listar cidades");
				System.out.println("<3> Cadastrar tipo de dose");
				System.out.println("<4> Listar tipos de dose");
				System.out.println("<5> Atualizar doses aplicadas por cidades");
				System.out.println("<6> Atualizar doses aplicadas para todas as cidades");
				System.out.println("<7> Consultar doses aplicadas por cidade");
				System.out.println("<8> Importar dados vacinômetro");
				System.out.println("<9> Exportar dados vacinômetro");
				System.out.println("<0> Sair");
				System.out.print("Opcao: ");
				op = in.nextInt();
				switch (op) {
				case 1:
					novaCidade();
					break;
				case 2:
					listarCidades();
					break;
				case 3:
					novoTipoDose();
					break;
				case 4:
					listarTiposDose();
					break;
				case 5:
					atualizarDosesPorCidade();
					break;
				case 6:
					atualizarDosesTodasCidade();
					break;
				case 7:
					consultarDosesPorCidade();
					break;
				case 8:
					importarDadosVacinometro();
					break;
				case 9:
					exportarDadosVacinometro();
					break;
				case 0:
					break;
				default:
					System.out.println("Opcao invalida!");
				}
			} catch (InputMismatchException e) {
				System.out.println("Opcao invalida!");
				in.next();
			}
		} while (op != 0);
		in.close();
	}

	private static void exportarDadosVacinometro() {
		deleteFile("vacinometro.csv");
		
		DosesAplicadas[] daVetor = getVetorDosesAplicadas();
		Arrays.sort(daVetor);
		
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("vacinometro.csv"), StandardCharsets.ISO_8859_1))) {
			bw.write("Município;Dose;Total Doses Aplicadas"); // cabeçalho do arquivo CSV
			bw.newLine();
			for (DosesAplicadas da : daVetor) {
				bw.write(String.format("%s;%s;%d", da.getCidade().getNome(), da.getTipoDose().getNome(),
						da.getQtdDosesAplicadas()));
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("Erro de leitura no arquivo");
		}

	}

	private static void atualizarDosesTodasCidade() {
		Cidade[] vetorListaCidades = getVetorListaCidades();
		if (vetorListaCidades.length == 0) {
			System.out.println("Erro: Cidades não cadastradas.");
			return;
		}
		TipoDose[] vetorTiposDoses = getVetorTiposDoses();
		if (vetorTiposDoses.length == 0) {
			System.out.println("Erro: Tipos de doses não cadastradas.");
			return;
		}

		DosesAplicadas[] vetorDosesAplicadas = getVetorDosesAplicadas();
		if (vetorDosesAplicadas.length > 0) {
			deleteFile("dosesaplicadas.obj");
		}

		Scanner in = new Scanner(System.in);
		for (Cidade cidade : vetorListaCidades) {
			System.out.println("Nome Cidade: " + cidade.getNome());
			for (TipoDose tipoDose : vetorTiposDoses) {
				int qtdDose = 0;
				System.out.print("Informe a quantidade de doses aplicadas do tipo " + tipoDose.getNome() + ": ");
				qtdDose = in.nextInt();
				DosesAplicadas da = new DosesAplicadas(cidade, tipoDose, qtdDose);
				gravarDosesAplicadas(da);
			}

			vetorDosesAplicadas = removerDosesAplicadas(vetorDosesAplicadas, cidade);

			String continuar = null;
			do {
				System.out.println("Deseja continuar atualizando dados da próxima cidade? [S] Sim - [N] Não");
				continuar = in.next();
				if (continuar.equalsIgnoreCase("N")) {
					gravarDosesAplicadasAppend(vetorDosesAplicadas);
					return;
				}
			} while (!continuar.equalsIgnoreCase("S"));
		}
	}

	private static DosesAplicadas[] removerDosesAplicadas(DosesAplicadas[] vetorDosesAplicadas, Cidade cidade) {
		DosesAplicadas[] vetorFiltrado = new DosesAplicadas[vetorDosesAplicadas.length];
		int i = 0;
		for (DosesAplicadas da : vetorDosesAplicadas) {
			if (!da.getCidade().equals(cidade)) {
				vetorFiltrado[i] = da;
				i++;
			}
		}
		return removeNulosVetor(vetorFiltrado);
	}

	private static void consultarDosesPorCidade() {
		try {
			Scanner in = new Scanner(System.in);
			System.out.print("Informe o nome da Cidade: ");
			String nomeCidade = in.nextLine();
			Cidade cidade = getCidade(nomeCidade);
			if (cidade == null) {
				System.out.println("Erro: Cidade informada não está cadastrada.");
				return;
			}
			DosesAplicadas[] dosesAplicadasPorCidade = vetorDosesAplicadasPorCidade(cidade);

			if (dosesAplicadasPorCidade.length == 0) {
				System.out.println("Erro: Não existe registro de doses aplicadas para essa cidade.");
				return;
			}
			System.out.printf("%-20s%-15s%15s\n", "Nome da Cidade", "Tipo de Dose", "Doses Aplicadas");
			System.out.println("--------------------------------------------------");
			for (DosesAplicadas da : dosesAplicadasPorCidade) {
				if (da != null)
					System.out.printf("%-20s%-15s%15d\n", da.getCidade().getNome(), da.getTipoDose().getNome(),
							da.getQtdDosesAplicadas());
			}

		} catch (InvalidPathException e) {
			System.out.println("Nao foi possivel encontrar o arquivo cidades.obj!");
		} catch (InputMismatchException e) {
			System.out.println("Erro de entrada de dados!");
		}
	}

	private static void atualizarDosesPorCidade() {
		try {
			Scanner in = new Scanner(System.in);
			System.out.print("Informe o nome da Cidade: ");
			String nomeCidade = in.nextLine();
			Cidade c = getCidade(nomeCidade);
			if (c == null) {
				System.out.println("Erro: Cidade informada não está cadastrada.");
				return;
			}
			TipoDose[] listaTiposDoses = getVetorTiposDoses();
			if (listaTiposDoses.length == 0) {
				System.out.println("Erro: Tipos de doses não cadastradas.");
				return;
			}
			DosesAplicadas[] vetorDosesAplicadas = getVetorDosesAplicadas();
			if (vetorDosesAplicadas.length > 0) {
				deleteFile("dosesaplicadas.obj");
				for (int i = 0; i < vetorDosesAplicadas.length; i++) {
					if (!vetorDosesAplicadas[i].getCidade().equals(c)) {
						gravarDosesAplicadas(vetorDosesAplicadas[i]);
					}
				}
			}

			for (TipoDose tipoDose : listaTiposDoses) {
				int qtdDose = 0;
				System.out.print("Informe a quantidade de doses aplicadas do tipo " + tipoDose.getNome() + ": ");
				qtdDose = in.nextInt();
				DosesAplicadas dosesAplicadas = new DosesAplicadas(c, tipoDose, qtdDose);
				gravarDosesAplicadas(dosesAplicadas);
			}
		} catch (InvalidPathException e) {
			System.out.println("Nao foi possivel encontrar o arquivo cidades.obj!");
		} catch (InputMismatchException e) {
			System.out.println("Erro de entrada de dados!");
		}
	}

	private static DosesAplicadas[] getVetorDosesAplicadas() {
		Path path = Paths.get("dosesaplicadas.obj");
		int qtdObjetos = qtdObjetos(path);
		DosesAplicadas[] daVetor = new DosesAplicadas[qtdObjetos];
		if (Files.exists(path)) {
			try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
				for (int i = 0; i < qtdObjetos; i++) {
					DosesAplicadas da = (DosesAplicadas) input.readObject();
					daVetor[i] = da;
				}
			} catch (EOFException e) {
			} catch (ClassNotFoundException e) {
				System.out.println("Tipo de objeto invalido!");
			} catch (IOException e) {
				System.out.println("Erro de leitura no arquivo");
			}
		}
		return daVetor;
	}

	private static void novoTipoDose() {
		try {
			Scanner in = new Scanner(System.in);
			System.out.print("Informe o nome do tipo de dose: ");
			String nomeTipoDose = in.nextLine();
			if (tipoDoseExiste(nomeTipoDose)) {
				System.out.println("Erro: ja' existe tipo de dose com esse nome");
				return;
			}
			gravarTipoDose(new TipoDose(nomeTipoDose.toUpperCase()));
		} catch (InvalidPathException e) {
			System.out.println("Nao foi possivel encontrar o arquivo tipodose.obj!");
		} catch (InputMismatchException e) {
			System.out.println("Erro de entrada de dados!");
		}
	}

	private static void novaCidade() {
		try {
			Scanner in = new Scanner(System.in);
			System.out.print("Informe o nome da Cidade: ");
			String nomeCidade = in.nextLine();
			if (getCidade(nomeCidade) != null) {
				System.out.println("Erro: ja' existe uma Cidade com esse nome");
				return;
			}
			gravarCidade(new Cidade(nomeCidade.toUpperCase()));
		} catch (InvalidPathException e) {
			System.out.println("Nao foi possivel encontrar o arquivo cidades.obj!");
		} catch (InputMismatchException e) {
			System.out.println("Erro de entrada de dados!");
		}
	}

	private static Cidade getCidade(String nomeCidade) {
		Path path = Paths.get("cidades.obj");
		Cidade c = null;
		if (Files.exists(path)) {
			try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
				while (true) {
					c = (Cidade) input.readObject();
					if (c.getNome().equalsIgnoreCase(nomeCidade)) {
						return c;
					}
					c = null;
				}
			} catch (EOFException e) {
				return c;
			} catch (ClassNotFoundException e) {
				System.out.println("Tipo de objeto invalido!");
			} catch (IOException e) {
				System.out.println("Erro de leitura no arquivo");
			}
		}
		return c;
	}

	private static boolean tipoDoseExiste(String nomeTipoDose) {
		Path path = Paths.get("tipodose.obj");
		if (Files.exists(path)) {
			try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
				while (true) {
					TipoDose td = (TipoDose) input.readObject();
					if (td.getNome().equalsIgnoreCase(nomeTipoDose)) {
						return true;
					}
				}
			} catch (EOFException e) {
				return false;
			} catch (ClassNotFoundException e) {
				System.out.println("Tipo de objeto invalido!");
			} catch (IOException e) {
				System.out.println("Erro de leitura no arquivo");
			}
		}
		return false;
	}

	private static void gravarCidade(Cidade cidade) {
		Path path = Paths.get("cidades.obj");
		if (Files.exists(path)) {
			try (FileOutputStream fos = new FileOutputStream("cidades.obj", true);
					AppendingObjectOutputStream output = new AppendingObjectOutputStream(fos)) {
				output.writeObject(cidade);
			} catch (FileNotFoundException e) {
				System.out.println("Nao foi possível abrir o arquivo cidades.obj!");
			} catch (IOException e) {
				System.out.println("Erro de escrita no arquivo cidades.obj!");
			}
		} else {
			try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(path))) {
				output.writeObject(cidade);
			} catch (IOException e) {
				System.out.println("Erro de escrita no arquivo cidades.obj!");
			}
		}
	}

	private static void gravarTipoDose(TipoDose tipoDose) {
		Path path = Paths.get("tipodose.obj");
		if (Files.exists(path)) {
			try (FileOutputStream fos = new FileOutputStream("tipodose.obj", true);
					AppendingObjectOutputStream output = new AppendingObjectOutputStream(fos)) {
				output.writeObject(tipoDose);
			} catch (FileNotFoundException e) {
				System.out.println("Nao foi possível abrir o arquivo cidades.obj!");
			} catch (IOException e) {
				System.out.println("Erro de escrita no arquivo cidades.obj!");
			}
		} else {
			try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(path))) {
				output.writeObject(tipoDose);
			} catch (IOException e) {
				System.out.println("Erro de escrita no arquivo cidades.obj!");
			}
		}
	}

	private static void gravarDosesAplicadas(DosesAplicadas da) {
		Path path = Paths.get("dosesaplicadas.obj");
		if (Files.exists(path)) {
			try (FileOutputStream fos = new FileOutputStream("dosesaplicadas.obj", true);
					AppendingObjectOutputStream output = new AppendingObjectOutputStream(fos)) {
				output.writeObject(da);
			} catch (FileNotFoundException e) {
				System.out.println("Nao foi possível abrir o arquivo dosesaplicadas.obj!");
			} catch (IOException e) {
				System.out.println("Erro de escrita no arquivo dosesaplicadas.obj!");
			}
		} else {
			try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(path))) {
				output.writeObject(da);
			} catch (IOException e) {
				System.out.println("Erro de escrita no arquivo dosesaplicadas.obj!");
			}
		}
	}

	private static void deleteFile(String filename) {
		Path path = Paths.get(filename);
		if (Files.exists(path)) {
			File f = new File(filename);
			if (!f.delete()) {
				System.out.println(filename + " delete falhou!");
			}
		}
	}

	private static void listarCidades() {
		Path path = Paths.get("cidades.obj");
		if (Files.exists(path)) {
			try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
				System.out.println("Nome da Cidade");
				while (true) {
					Cidade c = (Cidade) input.readObject();
					System.out.println(c.getNome());
				}
			} catch (EOFException e) {
				System.out.println("Fim dos registros");
			} catch (ClassNotFoundException e) {
				System.out.println("Tipo de objeto invalido!");
			} catch (IOException e) {
				System.out.println("Erro de leitura no arquivo");
			}
		} else {
			System.out.println("Não existe cidade cadastrada.");
		}
	}

	private static void listarTiposDose() {
		Path path = Paths.get("tipodose.obj");
		if (Files.exists(path)) {
			try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
				System.out.println("Tipo de Dose");
				while (true) {
					TipoDose td = (TipoDose) input.readObject();
					System.out.println(td.getNome());
				}
			} catch (EOFException e) {
				System.out.println("Fim dos registros");
			} catch (ClassNotFoundException e) {
				System.out.println("Tipo de objeto invalido!");
			} catch (IOException e) {
				System.out.println("Erro de leitura no arquivo");
			}
		} else {
			System.out.println("Não existe tipo de dose cadastrada.");
		}
	}

	private static TipoDose[] getVetorTiposDoses() {
		Path path = Paths.get("tipodose.obj");
		int qtdObjetos = qtdObjetos(path);
		TipoDose[] tipoDoses = new TipoDose[qtdObjetos];
		if (Files.exists(path)) {
			try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
				for (int i = 0; i < qtdObjetos; i++) {
					tipoDoses[i] = (TipoDose) input.readObject();
				}
			} catch (EOFException e) {
			} catch (ClassNotFoundException e) {
				System.out.println("Tipo de objeto invalido!");
			} catch (IOException e) {
				System.out.println("Erro de leitura no arquivo");
			}
		}
		return tipoDoses;
	}

	private static Cidade[] getVetorListaCidades() {
		Path path = Paths.get("cidades.obj");

		int qtdObjetos = qtdObjetos(path);
		Cidade[] cidades = new Cidade[qtdObjetos];
		if (Files.exists(path)) {
			try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
				for (int i = 0; i < qtdObjetos; i++) {
					cidades[i] = (Cidade) input.readObject();
				}
			} catch (EOFException e) {
			} catch (ClassNotFoundException e) {
				System.out.println("Tipo de objeto invalido!");
			} catch (IOException e) {
				System.out.println("Erro de leitura no arquivo");
			}
		}
		return cidades;
	}

	private static DosesAplicadas[] vetorDosesAplicadasPorCidade(Cidade cidade) {
		Path path = Paths.get("dosesaplicadas.obj");
		int qtdObjetos = qtdObjetos(path);
		DosesAplicadas[] daVetor = new DosesAplicadas[qtdObjetos];
		if (Files.exists(path)) {
			try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
				for (int i = 0; i < qtdObjetos; i++) {
					DosesAplicadas da = (DosesAplicadas) input.readObject();
					if (da.getCidade().equals(cidade))
						daVetor[i] = da;
				}
			} catch (EOFException e) {
			} catch (ClassNotFoundException e) {
				System.out.println("Tipo de objeto invalido!");
			} catch (IOException e) {
				System.out.println("Erro de leitura no arquivo");
			}
		}
		return removeNulosVetor(daVetor);
	}

	private static DosesAplicadas[] removeNulosVetor(DosesAplicadas[] vetor) {
		DosesAplicadas[] vetorTemp = new DosesAplicadas[vetor.length];
		int count = -1;
		for (DosesAplicadas s : vetor) {
			if (s != null) {
				vetorTemp[++count] = s;
			}
		}
		return Arrays.copyOf(vetorTemp, count + 1);
	}

	private static String[] removeNulosVetor(String[] vetor) {
		String[] vetorTemp = new String[vetor.length];
		int count = -1;
		for (String s : vetor) {
			if (s != null) {
				vetorTemp[++count] = s;
			}
		}
		return Arrays.copyOf(vetorTemp, count + 1);
	}

	private static int qtdObjetos(Path path) {
		int qtdObj = 0;
		if (Files.exists(path)) {
			try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
				while (true) {
					input.readObject();
					qtdObj = qtdObj + 1;
				}
			} catch (EOFException e) {
			} catch (IOException e) {
				System.out.println("Erro de leitura no arquivo");
			} catch (ClassNotFoundException e) {
				System.out.println("Tipo de objeto invalido!");
			}
		}
		return qtdObj;
	}

	private static void importarDadosVacinometro() {
		long qtdLinhas = qtdLinhasArquivoCSV();

		try (BufferedReader br = new BufferedReader(new FileReader("20220606_vacinometro.csv"))) {
			String[] vetCidadesObjSemRepeticao = new String[(int) qtdLinhas];
			String[] vetTiposDoses = new String[(int) qtdLinhas];
			DosesAplicadas[] vetDosesAplicadas = new DosesAplicadas[(int) qtdLinhas - 1];
			// pula primeira linha com nome das colunas
			br.readLine();
			int i = 0;
			int pos = 0;
			int posTipoDose = 0;
			String linha;
			while ((linha = br.readLine()) != null) {
				String[] colunas = linha.split(";");

				DosesAplicadas da = new DosesAplicadas(new Cidade(colunas[0]), new TipoDose(colunas[1]),
						Integer.parseInt(colunas[2]));
				// gravarDosesAplicadas(da);
				vetDosesAplicadas[i] = da;
				// sempre adiciona a primeira linha com valor
				if (i == 0) {
					vetCidadesObjSemRepeticao[pos] = colunas[0];
					pos++;
				} else {
					// adiciona cidade se ainda não estiver no vetor
					if (podeAdicionarString(vetCidadesObjSemRepeticao, colunas[0])) {
						vetCidadesObjSemRepeticao[pos] = colunas[0];
						pos++;
					}
				}
				if (i == 0) {
					vetTiposDoses[posTipoDose] = colunas[1];
					posTipoDose++;
				} else {
					if (podeAdicionarString(vetTiposDoses, colunas[1])) {
						vetTiposDoses[posTipoDose] = colunas[1];
						posTipoDose++;
					}
				}
				i++;
			}

			deleteFile("dosesaplicadas.obj");
			Arrays.sort(vetDosesAplicadas);
			gravarDosesAplicadas(vetDosesAplicadas);

			deleteFile("tipodose.obj");
			vetTiposDoses = removeNulosVetor(vetTiposDoses);
			for (String td : vetTiposDoses) {
				// if (!tipoDoseExiste(td)) {
				gravarTipoDose(new TipoDose(td));
				// }
			}
			// transforma vetor de string em vetor de ojetos cidade
			Cidade[] cidadesObj = getVetorCidades(vetCidadesObjSemRepeticao, pos);
			// ordena lista de cidades antes de gravar no arquibo .bin
			Arrays.sort(cidadesObj);
			// grava vetor de objetos cidade em arquivo .bin
			deleteFile("cidades.obj");
			for (Cidade cidade : cidadesObj) {
				// if (getCidade(cidade.getNome()) == null) {
				gravarCidade(cidade);
				// }
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void gravarDosesAplicadas(DosesAplicadas[] vetDosesAplicadas) {
		Path path = Paths.get("dosesaplicadas.obj");
		try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(path))) {
			for (DosesAplicadas da : vetDosesAplicadas) {
				output.writeObject(da);
			}
		} catch (IOException e) {
			System.out.println("Erro de escrita no arquivo dosesaplicadas.obj!");
		}
	}

	private static void gravarDosesAplicadasAppend(DosesAplicadas[] vetDosesAplicadas) {
		Path path = Paths.get("dosesaplicadas.obj");
		try (FileOutputStream fos = new FileOutputStream("dosesaplicadas.obj", true);
				AppendingObjectOutputStream output = new AppendingObjectOutputStream(fos)) {
			for (DosesAplicadas da : vetDosesAplicadas) {
				output.writeObject(da);
			}
		} catch (IOException e) {
			System.out.println("Erro de escrita no arquivo dosesaplicadas.obj!");
		}
	}

	private static long qtdLinhasArquivoCSV() {
		long qtdLinhas = 0;
		try {
			qtdLinhas = Files.lines(Paths.get("20220606_vacinometro.csv")).count();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return qtdLinhas;
	}

	private static Cidade[] getVetorCidades(String[] vetCidadesObjSemRepeticao, int length) {
		Cidade[] cidadesObj = new Cidade[length];
		for (int x = 0; x < length; x++) {
			Cidade cid = new Cidade(vetCidadesObjSemRepeticao[x]);
			cidadesObj[x] = cid;
		}
		return cidadesObj;
	}

	private static boolean podeAdicionarString(String[] vetor, String valor) {
		for (String item : vetor) {
			if (item != null && item.equalsIgnoreCase(valor)) {
				return false;
			}
		}
		return true;
	}

}
