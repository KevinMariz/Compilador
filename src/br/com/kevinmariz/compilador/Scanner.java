package br.com.kevinmariz.compilador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Scanner {
    private static final char EOF = 0;
    private static final int STATE_ID = 38;

    private BufferedReader input;
    private String line = null;
    private int lineIdx = 0;
    private int columnIdx = 0;

    private Token rollback = null;

    public Scanner(File input) throws FileNotFoundException {
        this.input = new BufferedReader(new FileReader(input));
    }

    public int getLine() {
        return lineIdx;
    }

    public int getColumn() {
        return columnIdx;
    }

    public Token nextToken() throws IOException, LexException {
        ScannerSt st = new ScannerSt();

        if (rollback != null) {
            st.token = rollback;
            rollback = null;
        } else {

            while (st.token == null && !st.exit) {
                st.ch = nextChar();

                switch (st.state) {
                case 0: {
                	if(st.ch == '+'){
                		st.token = new Token(TipoToken.OP_MAIS);
                	} else if(st.ch == '-'){
                		st.token = new Token(TipoToken.OP_MENOS);
                	} else if(st.ch == '*'){
                		st.token = new Token(TipoToken.OP_MULTIPLICACAO);//state 3
                	} else if(st.ch == '/'){
                		st.token = new Token(TipoToken.OP_DIVISAO);//state 4
                	} else if(st.ch == '('){
                		st.token = new Token(TipoToken.ABRE_PARENTESES);//state 5
                    } else if(st.ch == ')'){
                		st.token = new Token(TipoToken.FECHA_PARENTESES);//state 6
                    } else if(st.ch == '{'){
                		st.token = new Token(TipoToken.ABRE_CHAVES);//state 7
                    } else if(st.ch == '}'){
                		st.token = new Token(TipoToken.FECHA_CHAVES);//state 8
                    } else if(st.ch == ';'){
                		st.token = new Token(TipoToken.PONTO_VIRGULA);//state 9
                	} else if(st.ch == ','){
                		st.token = new Token(TipoToken.VIRGULA);//state 10
                    } else if(st.ch == '<'){
                    	st.lexema += st.ch;
                    	st.state = 16;
                    } else if(st.ch == '\''){
                    	st.lexema += st.ch;
                    	st.state = 13;
                    } else if(st.ch == '>'){
                    	st.lexema += st.ch;
                    	st.state = 18;
                    } else if(st.ch == '='){
                		st.lexema += st.ch;
                		st.state = 20;
                	} else if(st.ch == '!'){
                		st.lexema += st.ch;
                		st.state = 22;
                    } else if(st.ch == '&'){
                		st.lexema += st.ch;
                		st.state = 24;
                    } else if(st.ch == '|'){
                		st.lexema += st.ch;
                		st.state = 26;
                    } else if(st.ch == '.'){
                    	st.lexema += st.ch;
                    	st.state = 31;
                    }else if(st.ch == '"'){
                		st.lexema += st.ch;
                		st.state = 11;
                    }else if(st.ch == 'i'){
                		st.lexema += st.ch;
                		st.state = 34;
                    }else if(st.ch == 'p'){
                		st.lexema += st.ch;
                		st.state = 39;
                    }else if(st.ch == 's'){
                    	st.lexema += st.ch;
                    	st.state = 40;
                    }else if(st.ch == 'v'){
                    	st.lexema += st.ch;
                    	st.state = 41;
                    }else if(st.ch == 'c'){
                    	st.lexema += st.ch;
                    	st.state = 42;
                    }else if(st.ch == 'e'){
                    	st.lexema += st.ch;
                    	st.state = 58;
                    }else if(st.ch == 'm'){
                    	st.lexema += st.ch;
                    	st.state = 60;
                    }else if(st.ch == 'w'){
                    	st.lexema += st.ch;
                    	st.state = 59;
                    } else if (isNumber(st.ch)) {
                        st.lexema += st.ch;
                        st.state = 33;
                    } else if (isLetterOrUnderline(st.ch)) {
                        st.lexema += st.ch;
                        st.state = 38;
                    } else if (st.ch == 0) {
                        st.exit = true;
                    } else if (st.ch != ' ' && st.ch != '\n' && st.ch != '\t') {
                        throw new LexException(st.ch, lineIdx, columnIdx); 
                    }

                    break;
                }
                
                case 11:{ 
                	if(st.ch == '%'){
                		st.lexema += st.ch;
                		st.state = 27;
                	}else if(st.ch == '"'){
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.STRING, st.lexema);
                    }else if(st.ch != '"') {
                		st.lexema += st.ch;
                	} else{
                    	 throw new LexException(st.ch, lineIdx, columnIdx); 
                     }
                        break;
                     }
                
                case 13:{ 
                	if(st.ch != '\'') {
                		st.lexema += st.ch;
                		st.state = 14;
                	}else{
                		throw new LexException(st.ch, lineIdx, columnIdx);
                     }
                        break;
                     }
                
                case 14:{ 
                	if(st.ch == '\'') {
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.CHAR, st.lexema);
                	}else{
                		throw new LexException(st.ch, lineIdx, columnIdx);
                     }
                        break;
                     }
                
                case 16:{
                	if(st.ch == '=') {
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.OP_MENOR_IGUAL);
                	}else{
                		st.token = new Token(TipoToken.OP_MENORQUE);
                		rollback(st.ch);
                	}
                        break;
                     }
                
                case 18:{
                	if(st.ch == '=') {
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.OP_MAIOR_IGUAL);
                	}else{
                		st.token = new Token(TipoToken.OP_MAIORQUE);
                		rollback(st.ch);
                	}
                        break;
                     }
                
                case 20:{
                	if(st.ch == '=') {
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.OP_IGUALDADE);
                	}else{
                		st.token = new Token(TipoToken.OP_ATRIBUICAO);
                		rollback(st.ch);
                	}
                        break;
                     }
                
                case 22:{
                	if(st.ch == '=') {
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.OP_DIFERENTE);
                	}else{
                		throw new LexException(st.ch, lineIdx, columnIdx);
                	}
                        break;
                     }
                
                case 24:{
                	if(st.ch == '&') {
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.AND);
                	}else{
                		st.token = new Token(TipoToken.E_COM);
                		rollback(st.ch);
                	}
                        break;
                     }
           
                case 26:{
                	if(st.ch == '|') {
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.OR);
                	}else{
                		throw new LexException(st.ch, lineIdx, columnIdx);
                	}
                        break;
                     }
                
                case 27:{
                	if(st.ch == 'c'){
                		st.lexema += st.ch;
                		st.state = 28;
                	}else if(st.ch == 'd') {
                		st.lexema += st.ch;
                		st.state = 29;
                	}else if(st.ch == 'f'){
                		st.lexema += st.ch;
                		st.state = 30;
                	}else{
                		st.lexema += st.ch;
                		st.state = 11;
                	}
                	break;
                     }
                
                case 28: {
                	if(st.ch == '"'){
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.FORMAT_CHAR, st.lexema);
                	}else if(st.ch != '"'){
                		st.lexema += st.ch;
                		st.state = 11;
                	}else{
                		throw new LexException(st.ch, lineIdx, columnIdx);
                	}
                	break;
                }
                
                case 29: {
                	if(st.ch == '"'){
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.FORMAT_INT, st.lexema);
                	}else if(st.ch != '"'){
                		st.lexema += st.ch;
                		st.state = 11;
                	}else{
                		throw new LexException(st.ch, lineIdx, columnIdx);
                	}
                	break;
                }
                 
                case 30: {
                	if(st.ch == '"'){
                		st.lexema += st.ch;
                		st.token = new Token(TipoToken.FORMAT_FLOAT, st.lexema);
                	}else if(st.ch != '"'){
                		st.lexema += st.ch;
                		st.state = 11;
                	}else{
                		throw new LexException(st.ch, lineIdx, columnIdx);
                	}
                	break;
                }
                
                case 31: {
                if (isNumber(st.ch)) {
                 	st.lexema += st.ch;
                   	st.state = 32;
                 }else if(!isNumber(st.ch)){
                	 st.token = new Token(TipoToken.NUM_REAL, st.lexema);
                 		rollback(st.ch);
                 }
                     break;
                 }
                

                case 32: {
                    if (isNumber(st.ch)) {
                    	st.lexema += st.ch;
                    } else{
                    	st.token = new Token(TipoToken.NUM_REAL, st.lexema);
                    	rollback(st.ch);
                    }
                    break;
                }
                	
                case 33: {
                    if (isNumber(st.ch)) {
                    	st.lexema += st.ch;
                    }else if(st.ch == '.'){
                    	st.lexema += st.ch;
                    	st.state = 31;
                    } else{
                    	st.token = new Token(TipoToken.NUM_INTEIRO, st.lexema);
                    	rollback(st.ch);
                    }

                    break;
                }
                
                case 34: {
                    if (st.ch == 'n') {
                    	st.lexema += st.ch;
                    	st.state = 35;
                    }else if(st.ch == 'f'){
                    	st.lexema += st.ch;
                    	st.state = 37;
                    } else if(isLetterOrNumberOrUnderline(st.ch)){
                    	st.lexema += st.ch;
                    	st.state = 38;
                    }else{
                    	throw new LexException(st.ch, lineIdx, columnIdx);
                    }

                    break;
                }
                
                    case 35:
                        treatTipoVar(st, 't', 36);
                        break;
                        
                    case 36: {
                        if(isLetterOrNumberOrUnderline(st.ch)){
                        	st.lexema += st.ch;
                        	st.state = STATE_ID;
                        }else{
                        	st.token = new Token(TipoToken.INT);
                            rollback(st.ch);
                        }

                        break;
                    }
                    
                    case 37: {
                        if(isLetterOrNumberOrUnderline(st.ch)){
                        	st.lexema += st.ch;
                        	st.state = STATE_ID;
                        }else{
                        	st.token = new Token(TipoToken.R_IF);
                            rollback(st.ch);
                        }

                        break;
                    }

                    case 39:
                        treatTipoVar(st, 'r', 43);
                        break;
                    case 43:
                        treatTipoVar(st, 'i', 44);
                        break;
                    case 44:
                        treatTipoVar(st, 'n', 45);
                        break;
                    case 45:
                        treatTipoVar(st, 't', 46);
                        break;
                    case 46:
                        treatTipoVar(st, 'f', 74);
                        break;
                        
                    case 74:
                    	if(isLetterOrNumberOrUnderline(st.ch)){
                        	st.lexema += st.ch;
                        	st.state = STATE_ID;
                        }else{
                        	st.token = new Token(TipoToken.R_PRINTF);
                            rollback(st.ch);
                        }
                        break;
    
                    case 40:
                        treatTipoVar(st, 'c', 47);
                        break;
                    case 47:
                        treatTipoVar(st, 'a', 48);
                        break;
                    case 48:
                        treatTipoVar(st, 'n', 49);
                        break;
                    case 49:
                        treatTipoVar(st, 'f', 50);
                        break;

                    case 50:
                    	if(isLetterOrNumberOrUnderline(st.ch)){
                        	st.lexema += st.ch;
                        	st.state = STATE_ID;
                        }else{
                        	st.token = new Token(TipoToken.R_SCANF);
                            rollback(st.ch);
                        }
                        break;
                        
                    case 41:
                        treatTipoVar(st, 'o', 51);
                        break;
                    case 51:
                        treatTipoVar(st, 'i', 52);
                        break;
                    case 52:
                        treatTipoVar(st, 'd', 53);
                        break;

                    case 53:
                    	if(isLetterOrNumberOrUnderline(st.ch)){
                        	st.lexema += st.ch;
                        	st.state = STATE_ID;
                        }else{
                        	st.token = new Token(TipoToken.R_VOID);
                            rollback(st.ch);
                        }
                        break;
                        
                    case 42:
                        treatTipoVar(st, 'h', 54);
                        break;
                    case 54:
                        treatTipoVar(st, 'a', 55);
                        break;
                    case 55:
                        treatTipoVar(st, 'r', 56);
                        break;

                    case 56:
                    	if(isLetterOrNumberOrUnderline(st.ch)){
                        	st.lexema += st.ch;
                        	st.state = STATE_ID;
                        }else{
                        	st.token = new Token(TipoToken.R_CHAR);
                            rollback(st.ch);
                        }
                        break;

                    case 58:
                        treatTipoVar(st, 'l', 64);
                        break;
                    case 64:
                        treatTipoVar(st, 's', 65);
                        break;
                    case 65:
                        treatTipoVar(st, 'e', 66);
                        break;
                    
                    case 66:
                    	if(isLetterOrNumberOrUnderline(st.ch)){
                        	st.lexema += st.ch;
                        	st.state = STATE_ID;
                        }else{
                        	st.token = new Token(TipoToken.R_ELSE);
                            rollback(st.ch);
                        }
                        break;
                    
                    case 60:
                        treatTipoVar(st, 'a', 67);
                        break;
                    case 67:
                        treatTipoVar(st, 'i', 68);
                        break;
                    case 68:
                        treatTipoVar(st, 'n', 69);
                        break;
                    
                    case 69:
                    	if(isLetterOrNumberOrUnderline(st.ch)){
                        	st.lexema += st.ch;
                        	st.state = STATE_ID;
                        }else{
                        	st.token = new Token(TipoToken.R_MAIN);
                            rollback(st.ch);
                        }
                        break;
                        
                    case 59:
                        treatTipoVar(st, 'h', 70);
                        break;
                    case 70:
                        treatTipoVar(st, 'i', 71);
                        break;
                    case 71:
                        treatTipoVar(st, 'l', 72);
                        break;
                    case 72:
                        treatTipoVar(st, 'e', 73);
                        break;
                    
                    case 73:
                    	if(isLetterOrNumberOrUnderline(st.ch)){
                        	st.lexema += st.ch;
                        	st.state = STATE_ID;
                        }else{
                        	st.token = new Token(TipoToken.R_WHILE);
                            rollback(st.ch);
                        }
                        break;
                    
                    case STATE_ID: {
                        if (isLetterOrNumberOrUnderline(st.ch)) {
                            st.lexema += st.ch;
                        } else {
                            st.token = new Token(TipoToken.ID, st.lexema);
                            rollback(st.ch);
                        }
                        break;
                    }
                }
            }
        }

        System.out.println("LOG: " + (st.token != null ?
            st.token.getTipo().name() : "EOF"));

        return st.token;
    }

    private static boolean isNumber(char ch) {
        return Character.isDigit(ch);
    }


	private void treatTipoVar(ScannerSt state, char want, int nextState) {
        if (state.ch == want) {
            state.state = nextState;
            state.lexema += state.ch;
        } else if (isLetterOrNumberOrUnderline(state.ch)) {
            state.state = STATE_ID;
            state.lexema += state.ch;
        } else {
            state.token = new Token(TipoToken.ID, state.lexema);
            rollback(state.ch);
        }
    }

    private boolean isLetterOrUnderline(char ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    private boolean isLetterOrNumberOrUnderline(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }

    private void rollback(char ch) {
        if (ch != EOF) {
            columnIdx--;
        }
    }

    private char nextChar() throws IOException {
        char next;

        if (line == null || columnIdx >= line.length()) {
            line = input.readLine();
            columnIdx = 0;

            if (line != null) {
                lineIdx++;
                line += '\n';
            }
        }

        if (line == null) { //Fim do arquivo
            next = EOF;
        } else {
            next = line.charAt(columnIdx++);
        }

        return next;
    }

    public void rollbackToken(Token token) {
        rollback = token;
    }

    class ScannerSt {
        Token token = null;
        boolean exit = false;
        String lexema = "";
        int state = 0;
        char ch;
    }

}