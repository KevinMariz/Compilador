package br.com.kevinmariz.compilador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Main {

    private static int numLog = 1;

    private static BufferedWriter output;

    private static List<Token> tokens;
    private static SymbolTable symbolTable;
    private static Scanner scanner;
    private static String exp;

    private static Stack<String> pct;

    private static List<String> decl = new ArrayList<>();
    private static List<String> atrb = new ArrayList<>();

    public static void main(String[] args) throws LexException {
        /* Verifica se os caminhos dos arquivos de entra
           e saída foram passados como argumentos */
        if (args.length == 2) {
            String inputPath = args[0];
            String outputPath = args[1];

            File inputFile = new File(inputPath);
            File outputFile = new File(outputPath);

            /* Verifica se o caminho entrada é arquivo */
            if (inputFile.isFile()) {
                try {
                    output = new BufferedWriter(new FileWriter(outputFile));

                    /* Criando o analisador léxico */
                    scanner = new Scanner(inputFile);
                    symbolTable = new SymbolTable();
                    tokens = new ArrayList<>();

                    /* Chamando a análise sintática pelo
                       símbolo inicial */

                    symbolTable.initScope();
                    INICIAR();
                    symbolTable.finishScope();

/*                    Token token;
                    while ((token = scanner.nextToken()) != null) {
                        System.out.print(token);
                    }*/

                    for (String line : decl) {
                        output.write(line);
                        output.newLine();
                    }

                    output.newLine();

                    for (String line : atrb) {
                        output.write(line);
                        output.newLine();
                    }

                    output.close();

                    System.out.println("\n\n-- COMPILADO COM SUCESSO --");
                } catch (SinException sin) {
                    System.out.println("\n"+sin);
                } catch (LexException lex) {
                    System.out.println("\n" + lex);
                } catch (SymbolExistsException sex) {
                    System.out.println("\n" + sex);
                } catch (SymbolNotFoundException snf) {
                    System.out.println("\n" + snf);
                } catch (InvalidTypeException itp) {
                    System.out.println("\n" + itp);
                } catch (Exception other) {
                    other.printStackTrace();
                    showHelp();
                }
            } else {
                showHelp();
            }
        } else {
            showHelp();
        }
    }

    private static void INICIAR() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
    	Token token = scanner.nextToken();
    		
    	if(checkTipo(token, TipoToken.R_MAIN)){
    		token = scanner.nextToken();
    	}else{
    		throw new SinException(token, new TipoToken[]{TipoToken.R_MAIN}, scanner.getLine(), scanner.getColumn());
    	}
    	if(checkTipo(token, TipoToken.ABRE_PARENTESES)){
    		token = scanner.nextToken();
    	}else{
    		throw new SinException(token, new TipoToken[]{TipoToken.ABRE_PARENTESES}, scanner.getLine(), scanner.getColumn());
    	}
    	if(checkTipo(token, TipoToken.FECHA_PARENTESES)){
    		token = scanner.nextToken();
    	}else{
    		throw new SinException(token, new TipoToken[]{TipoToken.FECHA_PARENTESES}, scanner.getLine(), scanner.getColumn());
    	}
    	if(checkTipo(token, TipoToken.ABRE_CHAVES)){
    		PROGRAMA();
    		token = scanner.nextToken();
    	}else{
    		throw new SinException(token, new TipoToken[]{TipoToken.ABRE_CHAVES}, scanner.getLine(), scanner.getColumn());
    	}
        if(checkTipo(token, TipoToken.FECHA_CHAVES)){
        	//TODO: gerar codigo
        }else{
        	throw new SinException(token, new TipoToken[]{TipoToken.FECHA_CHAVES}, scanner.getLine(), scanner.getColumn());
        }
        
    }
    
    private static void PROGRAMA() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
    	LISTA_DECL();
        LISTA_CMD();
        
        Token token = scanner.nextToken();
    	if(token != null){
    		
   		if(checkTipo(token, TipoToken.R_IF)){
        	token = scanner.nextToken();
        	if(checkTipo(token, TipoToken.ABRE_PARENTESES)){
        		EXPRESSAO();
        		token = scanner.nextToken();
        	}else{
        		throw new SinException(token, new TipoToken[]{TipoToken.ABRE_PARENTESES}, scanner.getLine(), scanner.getColumn());
        	}
        	if(checkTipo(token, TipoToken.FECHA_PARENTESES)){

        		token = scanner.nextToken();
        	}else{
        		throw new SinException(token, new TipoToken[]{TipoToken.FECHA_PARENTESES}, scanner.getLine(), scanner.getColumn());
        	}
        	if(checkTipo(token, TipoToken.ABRE_CHAVES)){
        		PROGRAMA();
        		token = scanner.nextToken();
        	}else{
        		throw new SinException(token, new TipoToken[]{TipoToken.ABRE_CHAVES}, scanner.getLine(), scanner.getColumn());
        	}
        	if(checkTipo(token, TipoToken.FECHA_CHAVES)){
        		CHAM_ELSE();
        		PROGRAMA();
        	}else{
        		throw new SinException(token, new TipoToken[]{TipoToken.FECHA_CHAVES}, scanner.getLine(), scanner.getColumn());
        	}
    	}else if(checkTipo(token, TipoToken.R_WHILE)){
    		token = scanner.nextToken();
    		
        	if(checkTipo(token, TipoToken.ABRE_PARENTESES)){
        		EXPRESSAO();
        		token = scanner.nextToken();
        	}else{
        		throw new SinException(token, new TipoToken[]{TipoToken.ABRE_PARENTESES}, scanner.getLine(), scanner.getColumn());
        	}
        	if(checkTipo(token, TipoToken.FECHA_PARENTESES)){
        		token = scanner.nextToken();
        	}else{
        		throw new SinException(token, new TipoToken[]{TipoToken.FECHA_PARENTESES}, scanner.getLine(), scanner.getColumn());
        	}
        	if(checkTipo(token, TipoToken.ABRE_CHAVES)){
        		PROGRAMA();
        		token = scanner.nextToken();
        	}else{
        		throw new SinException(token, new TipoToken[]{TipoToken.ABRE_CHAVES}, scanner.getLine(), scanner.getColumn());
        	}
        	if(checkTipo(token, TipoToken.FECHA_CHAVES)){
            	PROGRAMA();
        		//gerar codigo
        	}else{
        		throw new SinException(token, new TipoToken[]{TipoToken.FECHA_CHAVES}, scanner.getLine(), scanner.getColumn());
        	}
    	}else{
    		scanner.rollbackToken(token);
    		}
    	}
    	}
    private static void CHAM_ELSE() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
    	Token token = scanner.nextToken();

    	if(token != null){
    		    		
    		if(checkTipo(token, TipoToken.R_ELSE)){
    			token = scanner.nextToken();
    			if(checkTipo(token, TipoToken.ABRE_CHAVES)){
    	    		PROGRAMA();
    	    		 token = scanner.nextToken();
    			}else{
     	    			throw new SinException(token, new TipoToken[]{TipoToken.ABRE_CHAVES}, scanner.getLine(), scanner.getColumn());
     	    		}
    	    	if(checkTipo(token, TipoToken.FECHA_CHAVES)){
    	        	PROGRAMA();
    	    		//TODO: gerar codigo
    	    	}else{
    	    		throw new SinException(token, new TipoToken[]{TipoToken.FECHA_CHAVES}, scanner.getLine(), scanner.getColumn());
    	    	}
    	    }else{
    		scanner.rollbackToken(token);
    	    }
    	}
    }
    
	private static void LISTA_DECL() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
    	DECL();
    	Token token = scanner.nextToken();
    	if (token != null) {
    		if (checkTipo(token, TipoToken.PONTO_VIRGULA)) {
    			LISTA_DECL();
    		}else{
    			scanner.rollbackToken(token);
    		}
    	}
    }
    
	private static void LISTA_CMD() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
		CMD();
		Token token = scanner.nextToken();
        if (token != null) {
            
    	if (checkTipo(token, TipoToken.PONTO_VIRGULA)){
    		LISTA_CMD();	
        }else{
        	scanner.rollbackToken(token);
        }
        }
    }

     private static void CMD() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        ATR();
        PRINT();
        SCAN();
    }

          private static void DECL() throws IOException, LexException, SymbolExistsException, SinException  {
        	  Token token = scanner.nextToken();
        	  
        	  	if(token != null){
        		  
        		if (checkTipo(token, TipoToken.INT) || checkTipo(token, TipoToken.FLOAT) || checkTipo(token, TipoToken.CHAR)){
        			LISTA_ID();
        	  	}else{
        		  	scanner.rollbackToken(token);
        	  	}
          }
          }
           
           private static void LISTA_ID() throws SinException, IOException, LexException, SymbolExistsException {
        	   ID();
        	   FIM_LISTA_ID();
           }

        private static void ID() throws SinException, IOException, LexException, SymbolExistsException {
        	 Token token = scanner.nextToken();
        	 
         	if (checkTipo(token, TipoToken.ID)){
         		ATR_ID();
         	}else{
         		throw new SinException(token, new TipoToken[]{TipoToken.ID}, scanner.getLine(), scanner.getColumn());
         	}
			
		}

		private static void FIM_LISTA_ID() throws SinException, IOException, LexException, SymbolExistsException {
        	   Token token = scanner.nextToken();
        	   
        	   if(token != null){
        		   
        		   if(checkTipo(token, TipoToken.VIRGULA)){
        			   LISTA_ID();
        		   }else{
        		   scanner.rollbackToken(token);
        	   }
        	   }
		}
        
		private static void ATR_ID() throws SinException, IOException, LexException, SymbolExistsException {
			Token token = scanner.nextToken();
			if(token != null){
				if(checkTipo(token, TipoToken.OP_ATRIBUICAO)){
					EXP();
				}else{
				scanner.rollbackToken(token);
				}
			}
		}
		
		private static void EXP() throws SinException, IOException, LexException, SymbolExistsException {
			OP();
			OPERACAO();
		}

	private static void OPERACAO() throws SinException, IOException, LexException, SymbolExistsException {
		Token token = scanner.nextToken();
		if(token != null){
			if(checkTipo(token, TipoToken.OP_MAIS) || checkTipo(token, TipoToken.OP_MENOS) || checkTipo(token, TipoToken.OP_MULTIPLICACAO) || checkTipo(token, TipoToken.OP_DIVISAO)){
				EXP();
			}else{
				scanner.rollbackToken(token);
			}
		}				
	}

	private static void OP() throws SinException, IOException, LexException, SymbolExistsException {
			Token token = scanner.nextToken();
//			if(token != null){
				if(checkTipo(token, TipoToken.ID) || checkTipo(token, TipoToken.NUM_INTEIRO) || checkTipo(token, TipoToken.NUM_REAL)){
					//TODO: gerer codigo
				}else{
					throw new SinException(token, new TipoToken[]{TipoToken.ID, TipoToken.NUM_INTEIRO, TipoToken.NUM_REAL}, scanner.getLine(), scanner.getColumn());
//					scanner.rollbackToken(token);
//				}
			}
			
		}
	
	private static void ATR() throws SinException, IOException, LexException, SymbolExistsException {
		Token token = scanner.nextToken();
		if(token != null){
		
		if(checkTipo(token, TipoToken.ID)){
			token = scanner.nextToken();
			if(checkTipo(token, TipoToken.OP_ATRIBUICAO)){
				EXP();
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.OP_ATRIBUICAO}, scanner.getLine(), scanner.getColumn());
			}
		}else{
			scanner.rollbackToken(token);
		}
		
		}
	}
	
	private static void PRINT() throws SinException, IOException, LexException, SymbolExistsException {
		Token token = scanner.nextToken();
		if(token != null){
			
		if(checkTipo(token, TipoToken.R_PRINTF)){
			token = scanner.nextToken();
			if(checkTipo(token, TipoToken.ABRE_PARENTESES)){
				P_PRINT();
				token = scanner.nextToken();
			}else{
					throw new SinException(token, new TipoToken[]{TipoToken.ABRE_PARENTESES}, scanner.getLine(), scanner.getColumn());
			}
			if(checkTipo(token, TipoToken.FECHA_PARENTESES)){
				//gerar codigo
			}else{
					throw new SinException(token, new TipoToken[]{TipoToken.ABRE_PARENTESES}, scanner.getLine(), scanner.getColumn());
			}
		}else{
			scanner.rollbackToken(token);
		}
		
		}
	}

	private static void P_PRINT() throws SinException, IOException, LexException, SymbolExistsException {
		Token token = scanner.nextToken();
		
		if(checkTipo(token, TipoToken.STRING)){
			
		}else if(checkTipo(token, TipoToken.FORMAT_INT)){
			token = scanner.nextToken();
			if(checkTipo(token, TipoToken.VIRGULA)){
				F_INT();
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA}, scanner.getLine(), scanner.getColumn());
			}	
		}else if(checkTipo(token, TipoToken.FORMAT_FLOAT)){
			token = scanner.nextToken();
			if(checkTipo(token, TipoToken.VIRGULA)){
				F_FLOAT();
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA}, scanner.getLine(), scanner.getColumn());
			}
		}else if(checkTipo(token, TipoToken.FORMAT_CHAR)){
			token = scanner.nextToken();
			if(checkTipo(token, TipoToken.VIRGULA)){
				F_CHAR();
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA}, scanner.getLine(), scanner.getColumn());
			}
		}else{
			throw new SinException(token, new TipoToken[]{TipoToken.STRING, TipoToken.FORMAT_INT, TipoToken.FORMAT_FLOAT, TipoToken.CHAR}, scanner.getLine(), scanner.getColumn());
		}
	}

	private static void SCAN() throws SinException, IOException, LexException, SymbolExistsException {
		Token token = scanner.nextToken();
		if(token != null){
		
		if(checkTipo(token, TipoToken.R_SCANF)){
			token = scanner.nextToken();
			if(checkTipo(token, TipoToken.ABRE_PARENTESES)){
				F_SCAN();
				token = scanner.nextToken();
			}else{
					throw new SinException(token, new TipoToken[]{TipoToken.ABRE_PARENTESES}, scanner.getLine(), scanner.getColumn());
			}
			if(checkTipo(token, TipoToken.FECHA_PARENTESES)){
				//gerar codigo
			}else{
					throw new SinException(token, new TipoToken[]{TipoToken.ABRE_PARENTESES}, scanner.getLine(), scanner.getColumn());
			}
		}else{
			scanner.rollbackToken(token);
		}
		
		}
	}
	
	private static void F_SCAN() throws SinException, IOException, LexException, SymbolExistsException {
		Token token = scanner.nextToken();
		if(checkTipo(token, TipoToken.FORMAT_INT)){
			token = scanner.nextToken();
			if(checkTipo(token, TipoToken.VIRGULA)){
				token = scanner.nextToken();
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA}, scanner.getLine(), scanner.getColumn());
			}
			if(checkTipo(token, TipoToken.E_COM)){
				token = scanner.nextToken();
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.E_COM}, scanner.getLine(), scanner.getColumn());
			}
			if(checkTipo(token, TipoToken.ID)){
				
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.ID}, scanner.getLine(), scanner.getColumn());
			}
		}else if(checkTipo(token, TipoToken.FORMAT_FLOAT)){
			token = scanner.nextToken();
			if(checkTipo(token, TipoToken.VIRGULA)){
				token = scanner.nextToken();
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA}, scanner.getLine(), scanner.getColumn());
			}
			if(checkTipo(token, TipoToken.E_COM)){
				token = scanner.nextToken();
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.E_COM}, scanner.getLine(), scanner.getColumn());
			}
			if(checkTipo(token, TipoToken.ID)){
				
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.ID}, scanner.getLine(), scanner.getColumn());
			}
		}else if(checkTipo(token, TipoToken.FORMAT_CHAR)){
			token = scanner.nextToken();
			if(checkTipo(token, TipoToken.VIRGULA)){
				token = scanner.nextToken();
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA}, scanner.getLine(), scanner.getColumn());
			}
			if(checkTipo(token, TipoToken.E_COM)){
				token = scanner.nextToken();
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.E_COM}, scanner.getLine(), scanner.getColumn());
			}
			if(checkTipo(token, TipoToken.ID)){
				
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.ID}, scanner.getLine(), scanner.getColumn());
			}
		}else{
			throw new SinException(token, new TipoToken[]{TipoToken.FORMAT_INT, TipoToken.FORMAT_FLOAT, TipoToken.CHAR}, scanner.getLine(), scanner.getColumn());
		}
	}
	
	private static void F_INT() throws SinException, IOException, LexException, SymbolExistsException {
		Token token = scanner.nextToken();
		if(checkTipo(token, TipoToken.NUM_INTEIRO)){
			//gerar codigo
		}else{
			throw new SinException(token, new TipoToken[]{TipoToken.NUM_INTEIRO}, scanner.getLine(), scanner.getColumn());
		}
	}
	
	private static void F_FLOAT() throws SinException, IOException, LexException, SymbolExistsException {
		Token token = scanner.nextToken();
		if(checkTipo(token, TipoToken.NUM_REAL)){
			//gerar codigo
		}else{
			throw new SinException(token, new TipoToken[]{TipoToken.NUM_REAL}, scanner.getLine(), scanner.getColumn());
		}
	}
	
	private static void F_CHAR() throws SinException, IOException, LexException, SymbolExistsException {
		Token token = scanner.nextToken();
		if(checkTipo(token, TipoToken.CHAR)){
			//gerar codigo
		}else{
			throw new SinException(token, new TipoToken[]{TipoToken.CHAR}, scanner.getLine(), scanner.getColumn());
		}
	}
	
	private static void EXPRESSAO() throws SinException, IOException, LexException, SymbolExistsException {
			EXPRESSAO_SIMPLES();
			OP_RELACIONAL();
			EXPRESSAO_SIMPLES();
	}

	private static void EXPRESSAO_SIMPLES() throws SinException, IOException, LexException, SymbolExistsException {
		FATOR();
	}

	private static void FATOR() throws SinException, IOException, LexException, SymbolExistsException {
		OP();
		Token token = scanner.nextToken();
		if(token != null){
			
		if(checkTipo(token, TipoToken.ABRE_PARENTESES)){
			EXPRESSAO();
			EXP();
			token = scanner.nextToken();
			if(checkTipo(token, TipoToken.FECHA_PARENTESES)){
//				TODO: gerar codigo
			}else{
				throw new SinException(token, new TipoToken[]{TipoToken.FECHA_PARENTESES}, scanner.getLine(), scanner.getColumn());}
		}else{
			scanner.rollbackToken(token);
		}
		}
	}
	
	
	private static void OP_RELACIONAL() throws SinException, IOException, LexException, SymbolExistsException {
		Token token = scanner.nextToken();
		if(checkTipo(token, TipoToken.OP_IGUALDADE) || checkTipo(token, TipoToken.OP_MENORQUE) || checkTipo(token, TipoToken.OP_MAIORQUE) 
			|| checkTipo(token, TipoToken.OP_MENOR_IGUAL) || checkTipo(token, TipoToken.OP_MAIOR_IGUAL) || checkTipo(token, TipoToken.OP_DIFERENTE) 
			|| checkTipo(token, TipoToken.OR) || checkTipo(token, TipoToken.AND)){
		}else{
			throw new SinException(token, new TipoToken[]{TipoToken.OP_MAIORQUE, TipoToken.OP_IGUALDADE, TipoToken.OP_MENORQUE, 
				TipoToken.OP_MENOR_IGUAL, TipoToken.OP_MAIOR_IGUAL, TipoToken.OP_DIFERENTE
					,TipoToken.OR, TipoToken.AND}, scanner.getLine(), scanner.getColumn());
		}
	}
	

	private static boolean checkTipo(Token token, TipoToken tipo) {
        return token != null && token.getTipo() == tipo;
    }

    private static void showHelp() {
        System.out.println("TODO: Help");
    }

    public static void log(SymbolTable sym) {
        //System.out.println(String.format("[%d]: %s", numLog++, sym));
    }
}
