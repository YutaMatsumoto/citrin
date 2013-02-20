
public class ExpressionEvaluator {

	private Interpreter interpreter = null;
	private Lexer lexer = null;	
	private Lexer.Token token;
	
	public static final String EQ = "==";
	public static final String LT = "<";	
	public static final String LE = "<=";
	public static final String GT = ">";
	public static final String GE = ">=";
	public static final String NE = "!=";
	
	//private constructor to prevent using default constructer
	@SuppressWarnings("unused")
	private ExpressionEvaluator(){
	}
	
	public ExpressionEvaluator(Interpreter i){
		interpreter = i;
		lexer = interpreter.lexer;
	}
	
	//entry point into parser
	public var_type eval_exp() {
	  var_type value;
	  token = lexer.get_token();
	  if(token.value==null) {
	    interpreter.sntx_err(/*NO_EXP*/);
	    return new var_type();
	  }
	  if(token.value.charAt(0) == ';') {
		interpreter.sntx_err();
	    /* TODO empty expression returns 0, probably should do something else? */
	    return new var_type();
	  }
	  value = eval_exp0();
	  lexer.putback(); /* return last token read to input stream */
	  return value;
	}

	// process an assignment expression
	private var_type eval_exp0() {
	  var_type value, result;
	  String temp; //holds name of var recieving assignment
	  token_type temp_tok;

	  if(token.type == token_type.IDENTIFIER) {
	    if(interpreter.is_var(token.value)) { /* if a var, see if assignment */
	      temp = new String(token.value);
	      temp_tok = token.type;
	      token = lexer.get_token();
	      if(token.value.charAt(0) == '=') { /* is an assignment */
	        token = lexer.get_token();
	        value = eval_exp0(); /* get value to assign */
	        result = interpreter.assign_var(temp, value); /* assign the value */
	        return result;
	      }
	      else { /* not an assignment */
	    	lexer.putback(); /* restore original token */
	        token.value = new String(temp);
	        token.type = temp_tok;
	      }
	    }
	  }
	  result = eval_exp1();
	  return result;
	}
	
	
	// process relational operators
	private var_type eval_exp1() {
	  var_type value, result;
	  var_type partial_value;
	  String op;

	  value = eval_exp2();
	  op = token.value;
	  
	  if(op.equals(EQ) || op.equals(LT) || op.equals(LE) || 
	      op.equals(GT) || op.equals(GE) || op.equals(NE)) {

		  token = lexer.get_token();
		  partial_value = eval_exp2();
		  result = value.relationalOperator(partial_value, op);
		  
	  }
	  else{
		  result = value;
	  }
	  
	  return result;
	}

	// add or subtract two terms
	private var_type eval_exp2() {
	  var_type value, partial_value;
	  char op = '\0';
	  var_type result;

	  value = eval_exp3();
	  result = new var_type(value);
	  while((op=token.value.charAt(0))=='+' || op=='-' ){
	    token = lexer.get_token();
	    partial_value = eval_exp3();
	    switch(op){
	      case '-':
	    	result = value.sub(partial_value);
	        break;
	      case '+':
	      	result = value.add(partial_value);
	        break;
	    }	
	  }
	  return result;
	}


	// multiply or divide two factors
	private var_type eval_exp3() {
	  var_type value, partial_value;
	  var_type result;
	  char op;
	  
	  value = eval_exp4 ();
	  result = new var_type(value);
	  while((op=token.value.charAt(0)) == '*' || op == '/' || op == '%' ){		
	    token = lexer.get_token();
	    partial_value = eval_exp4();
	    switch(op){
	      case '*':
	    	result = value.mul(partial_value);
	        break;
	      case '/':
	    	result = value.div(partial_value);
	        break;
	      case '%':
	      	result = value.mod(partial_value);
	        break;
	    }
	  }

	  return result;
	}
	
	
	//evaluate unary plus or minus
	private var_type eval_exp4() {
	  var_type value, result;
	  char op = '\0';

	  if(token.value.charAt(0) == '+' || token.value.charAt(0) == '-'){
	    op = token.value.charAt(0);
	    token = lexer.get_token();
	  }
	  value = eval_exp5();
	  result = new var_type(value);
	  if(op!='\0')
	    if(op == '-') result = value.unaryMinus();

	  return result;
	}

	private var_type eval_exp5() {
	  var_type value;
	  if((token.value.charAt(0) == '(')) {
	    token = lexer.get_token();
	    value = eval_exp0(); // get subexpression
	    if(token.value.charAt(0) != ')') interpreter.sntx_err(/*parenthesis expected*/);
	    token = lexer.get_token();
	  }
	  else
	    value = atom();

	  return value;
	}

	private var_type atom() {
	  //int i=0;
	  var_type value = new var_type();
	  switch(token.type){
	    case IDENTIFIER:
	      /*TODO:
	      //i = internal_func(token);
	      if(i != -1){ //call std library function
	      value  = (intern_func[i].p)();

	      }
	      else*/ 
	    	//check if function
	    	if(interpreter.isUserFunc(token.value)) { // call user defined function
	    	  value = interpreter.call(token.value);
	      	}
	      else value = interpreter.find_var(token.value); // get var's value
	      token = lexer.get_token();
	      return value;

	    case NUMBER: // is a numeric constant
	      if(token.value.indexOf('.') == -1){ // is an integer
	    	  value.value = Integer.parseInt(token.value);   
	    	  value.v_type = keyword.INT;
	      }
	      else{
	    	  value.value = Double.parseDouble(token.value);
	    	  value.v_type = keyword.DOUBLE;
	      }
	      token = lexer.get_token();
	      return value;

	    case CHAR: //check if character constant
	    	value.v_type = keyword.CHAR;
	    	value.value = (int) token.value.charAt(0);
	    	token = lexer.get_token();
	    	return value;
	      
	    case DELIMITER: 
	      //process empty expression return 0 TODO: verify this works 
	      if(token.value.charAt(0) == ')') return value; 
	      else interpreter.sntx_err();
	    default:
	      interpreter.sntx_err();
	      }
	  return value;
	  }
	
	
	
}
