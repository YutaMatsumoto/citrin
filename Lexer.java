import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Math;


public class Lexer {

  private final int IGNORE_CHAR = -1;
  
  //public String token = null;
  private int lastIndex = 0;
  //private token_type type = token_type.TEMP;
  private char prog[] = null;
  public int index = 0;
  //public keyword tok = null;
  
  public commands table[] = { /* Commands must be entered in lowercase in this table?*/
		    new commands("if", keyword.IF), 
		    new commands("else", keyword.ELSE),
		    new commands("for", keyword.FOR),
		    new commands("do", keyword.DO),
		    new commands("while", keyword.WHILE),
		    new commands("char", keyword.CHAR),
		    new commands("short", keyword.SHORT),
		    new commands("int", keyword.INT),
		    new commands("return", keyword.RETURN),
		    new commands("end", keyword.END),
		    new commands("float", keyword.FLOAT),
		    new commands("bool", keyword.BOOL),
		    new commands("double", keyword.DOUBLE),
		    new commands("", keyword.END) /* mark end of table */
		  };
  
  void loadSourceFile( String fileName ){
	  
	  String temp = null;
	  
	    try {
	        temp = new Scanner( new File(fileName) ).useDelimiter("\\A").next();
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    //TODO need to add a check if temp was loaded successfully
	    prog = temp.toCharArray();
  }
  
  
  /* Get a token. */
  //TODO make sure wont go outside boundary
  public Token get_token() {
	  
    //token_type type = token_type.TEMP; //replaced with global
	Token token = new Token();
		
    int temp;

    lastIndex = index;
    
    /* skip over white space and comments */
    boolean done = false;
    while(!done){
    	done = true;
    	//skip white space
        while(index<prog.length && Character.isWhitespace(prog[index])) index++;    	
    	
        // skip comments
        if( index+1 < prog.length && prog[index] == '/' && prog[index+1] == '*'){
          done = false;
          index += 2;
          while (index+1 < prog.length && (prog[index]!='*' || prog[index+1]!='/') )
        	  index++;
          index+=2;
        }

    }


    if(index >= prog.length) { /* end of file */
      token.value = "";
      token.key = keyword.FINISHED;
      token.type = token_type.DELIMITER;
      return (token);
    }

    if( "{}".indexOf( prog[index] )>=0 ) { /* block delimiters */
      token.value = Character.toString(prog[index]);
      index++;
      token.type = token_type.BLOCK;
      return (token);
    }

    if( "!<>=".indexOf(prog[index])>=0 ) { /* is or might be a relational operator */
      switch(prog[index]) {
        case '=': 
          if(index+1 < prog.length && prog[index+1] == '=') {
            index += 2;
            token.value = "==";
          }
          break;
        case '!': 
          if(index+1 < prog.length && prog[index+1] == '=') {
            index+=2;
            token.value = "!=";
          }
          break;

        case '<': 
          if(index+1 < prog.length && prog[index+1] == '=') {
            index += 2;
            token.value = "<=";
          }
          else {
            index++;
            token.value = "<";
          }
          break;

        case '>': 
          if(index+1 < prog.length && prog[index+1] == '=') {
            index += 2;
            token.value = ">=";
          }
          else {
            index++;
            token.value = ">";
          }
          break;
      }

      if(token.value != null){
    	  token.type = token_type.DELIMITER;
    	  return (token);	
      }
    }

    if(prog[index]=='"') { /* quoted string */
      index++;
      StringBuffer buf = new StringBuffer();
      while(index<prog.length && prog[index]!='"' && prog[index]!='\r' && prog[index]!='\n'){
    	
    	if(prog[index]=='\\'){ //check for escape char
    		index++;
    		temp = getEscapeCharacter();
    		if(temp!=IGNORE_CHAR)
    			buf.append((char)temp);
    	}
    	else{
    		buf.append(prog[index]);
    	}
        index++;
      }
      if(prog[index] == '\r' || prog[index] == '\n' || index>=prog.length ) 
        sntx_err(/*expecting "*/);//call syntax error

      index++;
      token.value = buf.toString();
      token.type = token_type.STRING;
      return (token);
    }

    //TODO: multi character constants have only charAt(0) used, this is handled by atom()...
    if(prog[index]=='\'') { /* character bounded by ' */
    	index++;
        StringBuffer buf = new StringBuffer();
        while(index<prog.length && prog[index]!='\'' && prog[index]!='\r' && prog[index]!='\n'){
      	
      	if(prog[index]=='\\'){ //check for escape char
      		index++;
      		temp = getEscapeCharacter();
      		if(temp!=IGNORE_CHAR)
      			buf.append((char)temp);
      	}
      	else{
      		buf.append(prog[index]);
      	}
          index++;
        }
        if(prog[index] == '\r' || prog[index] == '\n' || index>=prog.length ) 
          sntx_err(/*expecting '*/);//call syntax error

        index++;
        token.value = buf.toString();
        token.type = token_type.CHAR;
        return (token);
      }
    
    if( "+-*^/%=;(),'".indexOf(prog[index])>=0 ){ /* delimiter */
        token.value = Character.toString(prog[index]);
        index++;
        token.type = token_type.DELIMITER;
        return (token);
      }
    
    if(Character.isDigit(prog[index]) || 
    		(prog[index]=='.' && Character.isDigit(prog[index+1]) ) ) { /* number */
      int numDecimals = 0;
      StringBuffer buf = new StringBuffer();
      while( index<prog.length && Character.isDigit(prog[index]) || prog[index]=='.' ){
        buf.append(prog[index]);
        if(prog[index]=='.')
        	numDecimals++;
        if(numDecimals > 1) sntx_err(/*too many decimals in number*/);
        index++;
      }
      token.value = buf.toString();
      token.type = token_type.NUMBER;
      return (token);
    }

    if(Character.isLetter(prog[index])) { /* var or command */
      StringBuffer buf = new StringBuffer();
      while(Character.isLetterOrDigit(prog[index]) || prog[index] == '_'){
    	buf.append(prog[index]);
        index++;
      }
      token.value = buf.toString();
      token.type = token_type.TEMP;
    }

    /* see if a string is a command or a variable */
    if(token.type==token_type.TEMP) {
      token.key = look_up(token.value); /* convert to internal rep */
      if(token.key != null) token.type = token_type.KEYWORD; /* is a keyword */
      else token.type = token_type.IDENTIFIER;
    }
    
    // if token is still null something is wrong
    if(token.value==null)
    	sntx_err(/*stray token/unkown token*/);
    
    return token;

  }

  //function to handle escape secuences, 
  //index should be pointing to the character after the '\'
  //http://en.cppreference.com/w/cpp/language/escape
  private int getEscapeCharacter(){
	if(index>=prog.length){
		sntx_err();
		return IGNORE_CHAR;
	}
	switch(prog[index]){
	case '\\': return '\\';
	case '\'': return '\'';
	case '\"': return '\"';
	case 'a': return (char) 7;
	case 'b': return '\b';
	case 'f': return '\f';
	case 'n': return '\n';
	case '?': return '?'; 
	
	case 'N':
	case 'u':
	case 'U': sntx_err(/*Unicode not supported*/); return IGNORE_CHAR;
	
	case 'r': return '\r';
	case 't': return '\t';
	case 'v': return (char) 13;
	
	default: 
		if(Character.isDigit(prog[index])){
			//octal escape
			int j=0;
			StringBuffer octalStr = new StringBuffer(); 
			
			//form string of digits
			for(j=0; (j<3) && (index+j<prog.length); ++j ){
				if(!Character.isDigit(prog[index+j]))
					break;
				else
					octalStr.append(prog[index+j]);
			}
			
			String oct = octalStr.toString();
			
			//check for ints that aren't octal
			if(oct.indexOf('9') >= 0 || oct.indexOf('8') >= 0){
				sntx_err(/* not an octal char*/);
			}
			
			//parse string to int
			int octal = Integer.parseInt(oct.toString(), 8);
			if(octal>256)
				sntx_err(/*octal char out of range*/);
            index += j-1;
            return (char) octal;
		}
		
		else if(prog[index]=='x'){
			//hexadecimal escape
			index++;
			int j=0;
			StringBuffer hexStr = new StringBuffer(); 
			char temp;
			
			//form string of digits
			for(j=0; (j<2) && (index+j<prog.length); ++j ){
				temp = Character.toLowerCase(prog[index+j]);
				if(!(Character.isDigit(temp) || ('a'<=temp && 'f'>=temp) ) )
					break;
				else
					hexStr.append(temp);
			}
			
			if(j == 0){
				sntx_err(/*\x used with no hex digits*/);
				return IGNORE_CHAR;
			}
			//parse string to int
			int hex = Integer.parseInt(hexStr.toString(), 16);
            index += j-1;
            return (char) hex;
		}
		
		else{
			sntx_err(/*unrecognized escape sequence, also known as unrecognized char*/);
		}
	}
	
	return IGNORE_CHAR;
  }
	  
  public keyword look_up(String key) {
	    int i=-1;
	    key = new String(key.toLowerCase());
	    for(i = 0; i<table.length ;i++){
	      if(key.equals(table[i].command)) return table[i].tok;
	    }
	    return null; // unkown command
	  }

  
  private void sntx_err() {
	    System.out.println("lexer error");
	    // TODO Auto-generated method stub

	  }
  
  public void putback(){
  index = lastIndex;
  }

  
	  
  public class commands {
		public commands(String c, keyword t){
			command = c;
			tok = t;
		}
	public String command;
	public keyword tok;
	}
	  
  public class Token {
	  String value = null;
	  token_type type = null;
	  keyword key = null;
	  
	  //TODO temporary override to remind myself to call token.value.equals, not token.equals
	  @Override
	  public boolean equals(Object other){
		  System.out.println("you called token.equals, instead of token.value.equals");
		  int a = 1/0;
		  return a==0;
	  }
  }
  
}
