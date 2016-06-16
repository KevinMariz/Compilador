package br.com.kevinmariz.compilador;

public enum TipoToken {

	STRING,
	CHAR,
	INT,
	FLOAT,
	NUM_INTEIRO,
	NUM_REAL,
	ID,
	OP_MAIORQUE,
	OP_MENORQUE,
	OP_MENOR_IGUAL,
	OP_MAIOR_IGUAL,
	OP_ATRIBUICAO,
	OP_IGUALDADE,
	OP_DIFERENTE,
	OP_MAIS,
	OP_MENOS,
	OP_DIVISAO,
	OP_MULTIPLICACAO,
	ABRE_PARENTESES,
	FECHA_PARENTESES,
	ABRE_CHAVES,
	FECHA_CHAVES,
	E_COM,
	OR,
	AND,
	PONTO_VIRGULA,
	VIRGULA,
	R_PRINTF,
	R_SCANF,
	R_VOID,
	R_CHAR,
	R_IF,
	R_ELSE,
	R_MAIN,
	R_WHILE,
	FORMAT_INT,
	FORMAT_FLOAT,
	FORMAT_CHAR,


//    String nick;
//
//    TipoToken(String nick) {
//        this.nick = nick;
//    }
//
//    @Override
//    public String toString() {
//        return nick;
//    }
}