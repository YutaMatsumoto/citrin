import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Math;

//TODO should derive functions and vars from same base class to handle functions as parameters
//TODO should add base class for basic vars and classes

public class Interpreter {

  //public enum double_ops {LT, LE, GT, GE, EQ, NE};
  private enum block_type {FUNCTION, CONDITIONAL};
  private enum return_state{FUNC_RETURN, END_OF_BLOCK};

  //private final int NUM_COMMANDS = 14;
  
  private Lexer.Token token;

  
  private int lvartos=0; /* index into local variable stack */
  private var_type ret_value; /* function return value */
  private int functos = 0; /* index to top of function call stack */

  //TODO: need to look at removing the hard limits for variables or adding checks/error messages
  
  private final int NUM_LOCAL_VARS = 200;
  private var_type local_var_stack[] = new var_type[NUM_LOCAL_VARS];
  private final int NUM_FUNC = 100;
  private func_type func_table[] = new func_type[NUM_FUNC];
  private int func_index = 0; // index into function table
  int call_stack[] = new int[NUM_FUNC];
  private final int NUM_GLOBAL_VARS = 100;
  private var_type global_vars[] = new var_type[NUM_GLOBAL_VARS];
  private int gvar_index = 0; /* index into global variable table */
  
  private int NUM_PARAMS = 20;

  static private String interpretation = "";
  Lexer lexer = null;

  public static void main(String[] args) throws IOException {
    /*if(args.length != 1) {
      System.out.println("No file given \n");
      System.exit(0);
      }*/

    // init interpretation result string
    interpretation = "";

    //load program
    Interpreter interpreter = new Interpreter();
    interpreter.runAll("testCode.cpp");

    System.out.print(interpretation);
  }


  public String runAll(String CppSrcFile) throws IOException {

    interpretation = "";

    lexer = new Lexer();
    lexer.loadSourceFile(CppSrcFile);

    gvar_index = 0; /* initialize global variable index */
    lvartos = 0; /* initialize local variable stack index */
    functos = 0; /* initialize the CALL stack index */

    int index = -1;
    prescan(); /* find the location of all functions and global variables in the program */
    

    if(!isUserFunc("main")){
    	interpretation+="main() not found";
    	return interpretation;
    }
    ArrayList<var_type> args = new ArrayList<var_type>();
    index = find_func("main", args); //set up call to main    
    lexer.index = func_table[index].location;
	  int lvartemp = lvartos; //save local var stack index
	  func_push(lvartemp); //save local var stack index
    
    //TODO: need special call main
    interp_block(block_type.FUNCTION);
    func_pop();
    
    return interpretation;
  }

  public void test() throws IOException {
    lexer = new Lexer();
    lexer.loadSourceFile("testCode.cpp");

    token = lexer.get_token();
    while(token.key!=keyword.FINISHED){
      lexer.get_token();
      System.out.println(token.value);
    }

  }

  //TODO I used charAt(0) alot without additional checks... This is probably bad

  public boolean isdelim(char c){
    if( "+-/*%^=()".indexOf(c)>=0 || c==0 || Character.isWhitespace(c))
      return true;
    else
      return false;
  }


  public void sntx_err() {
    interpretation+="ERROR\n";
    System.out.println("error");
    // TODO Auto-generated method stub
    int a =1/0;

  }
  
  
public void warning(){
	  interpretation+="WARNING\n";
	  System.out.println("warning");
  }

  /* Interpret a single statement or block of code. When
     interp_block() returns from its initial call, the final
     brace (or a return) in main() has been encountered.
     */
  public return_state interp_block(block_type b_type){
    int block = 0;

    /* If interpreting single statement, return on
    first semicolon.*/
    do {
    	
      waitIfNeeded();
    	
      token = lexer.get_token();
      /* see what kind of token is up */
      if(token.type==token_type.BLOCK) { /* if block delimiter */
          if(token.value.charAt(0) == '{' && block==0) /* is a block */
            block = 1; /* interpreting block, not statement */
          else if(token.value.equals("{") && block!=0)
        	  sntx_err(/*why is a { here*/);
          else{
          	return return_state.END_OF_BLOCK; /* is a }, so return */
          }
      }
      else if(token.type == token_type.IDENTIFIER || token.type == token_type.NUMBER || token.type==token_type.CHAR) {
        /* Not a keyword, so process expression. */
        lexer.putback(); /* restore token to input stream for
                           further processing by eval_exp() */
        ExpressionEvaluator eval = new ExpressionEvaluator(this);
        eval.eval_exp(); /* process the expression */
        token = lexer.get_token();
        
        if(token.value.charAt(0)!=';') sntx_err(/*expecting semi*/);
      }
      else if(token.value.equals(";")){
    	  // empty statement do nothing
      }
    else /* is keyword */
      switch(token.key) {

      case SHORT:
      case FLOAT:
      case BOOL:
      case DOUBLE:
      case CHAR:
      case INT: // declare local variables
          lexer.putback();
          decl_local();
          break;
      case RETURN: // return from function call 
    	  if(b_type == block_type.FUNCTION)
    		  func_ret();
    	  else{
    		  // if in a conditional block (if,while,..) put the return token back
    		  // return to previous interp_block statement
    		  lexer.putback();  
    	  }
          return return_state.FUNC_RETURN;
      case IF: // process an if statement 
          exec_if();
          break;
      /*case ELSE: // process an else statement 
          find_eob(); // find end of else block and continue execution 
          break;*/
      case WHILE: // process a while loop 
          exec_while();
          break;
      case DO: // process a do-while loop 
          exec_do();
          break;
      default:
    	  sntx_err();
      }
      
  } while (token.key != keyword.FINISHED && block!=0);
    
  return return_state.END_OF_BLOCK;
  
}
  
private void waitIfNeeded(){
	//TODO
}
  
public var_type assign_var(String var_name, var_type value) {
  int i;
  var_type result;
  // check if its a local variable
  // TODO: check this... the indexes for loops look iffy
  for(i=lvartos-1;i >= call_stack[functos - 1]; i--){
    if(var_name.equals(local_var_stack[i].var_name)){
      local_var_stack[i].assignVal(value);
      printVarVal(local_var_stack[i]);
      result = new var_type(local_var_stack[i]);
      return result;
    }
  }

    if(i < call_stack[functos-1]) // if not local try global
    	for(i = 0; i < gvar_index; i++)
    		if(var_name.equals(global_vars[i].var_name)){
    			global_vars[i].assignVal(value);
    			printVarVal(global_vars[i]);
    		    result = new var_type(global_vars[i]);
    		    return result;
    		} 

  sntx_err(/*var not found*/);
  return new var_type();
}

void printVarVal(var_type v){
    if(v.v_type == keyword.INT || v.v_type == keyword.SHORT || v.v_type == keyword.BOOL )
  	  interpretation += ( v.var_name + " = " + v.value.intValue() + "\n");
    else if(v.v_type == keyword.CHAR)
  	  interpretation += ( v.var_name + " = " + (char)(v.value.intValue()) + "\n");    	  
    else if(v.v_type == keyword.FLOAT || v.v_type  == keyword.DOUBLE)
  	  interpretation += ( v.var_name + " = " + v.value.doubleValue() + "\n");
}

public boolean is_var(String var_name) {
	int i;

  // check if its a local variable
  for(i=lvartos-1;i >= call_stack[functos - 1]; i--){
    if(var_name.equals(local_var_stack[i].var_name)){
      return true;
    }
  }

    if(i < call_stack[functos-1]) // if not local try global
    	for(i = 0; i < gvar_index; i++)
    		if(var_name.equals(global_vars[i].var_name)){
    			return true;
    		} 

  return false;
}



public void run_err() {
  interpretation += "Run Error";
  // System.out.println("Run Error");
  // TODO Auto-generated method stub

}



// declare global
public void decl_global(){
	  var_type i = new var_type();
	  token = lexer.get_token();
	  i.v_type = token.key; //get token type
	  ExpressionEvaluator eval = new ExpressionEvaluator(this);
	  var_type value;
	  
	  do { // process comma separated list
		  i.value = 0; // init to 0
		  token = lexer.get_token(); //get name
		  i.var_name = new String(token.value);
		  
		  //check for initialize
		  token = lexer.get_token();
		  if(token.value.equals("=")){ 
			  value = eval.eval_exp();
			  i.assignVal(value);
		  }
		  else{
			  lexer.putback();
		  }
		  
		  //push var onto stack
		  var_type v = global_push(i);
		  printVarVal(v);
		  
	      token = lexer.get_token();
	  } while(token.value.equals(","));
	  if(token.value.charAt(0) != ';') sntx_err(/*SEMI_EXPECTED*/);
}

  /* Declare a local variable. */
  private void decl_local(){
    var_type i = new var_type();
    token = lexer.get_token(); /* get type */
    i.v_type = token.key;
	ExpressionEvaluator eval = new ExpressionEvaluator(this);
	var_type value;

    do { /* process comma-separated list */
      i.value = 0; /* init to 0 should remove this*/
      token = lexer.get_token(); /* get var name */
      i.var_name = new String(token.value);
      
	  //check for initialize
	  token = lexer.get_token();
	  if(token.value.equals("=")){ //initialize
		  value = eval.eval_exp();
		  i.assignVal(value);
	  }
	  else{
		  lexer.putback();
	  }
	  
	  //push var onto stack
      var_type v=local_push(i);
      printVarVal(v);
      
      token = lexer.get_token();
    } while( token.value.charAt(0) == ',');
    if(token.value.charAt(0) != ';') sntx_err(/*SEMI_EXPECTED*/);
  }

  private var_type global_push(var_type i) {
	    if(gvar_index > NUM_GLOBAL_VARS)
	      sntx_err(/* too many global vars*/);

	    global_vars[gvar_index] = new var_type(i);
	    gvar_index++;
	    return global_vars[gvar_index-1];
	  }
  
  private var_type local_push(var_type i) {
    if(lvartos > NUM_LOCAL_VARS)
      sntx_err(/* too many local vars*/);

    local_var_stack[lvartos] = new var_type(i);
    lvartos++;
    return local_var_stack[lvartos-1];
  }

  public var_type find_var(String var_name) { 
    int i;

    // check if its a local variable
    for(i=lvartos-1;i >= call_stack[functos - 1]; i--){
      if(var_name.equals(local_var_stack[i].var_name)){
        return local_var_stack[i];
      }
    }

      if(i < call_stack[functos-1]) // if not local try global
      	for(i = 0; i < gvar_index; i++)
    		if(var_name.equals(global_vars[i].var_name)){
    			  return global_vars[i];
    		  }

    sntx_err(/*var not found*/);
    return new var_type();
  }

  
  void exec_while(){
	  var_type cond;
	  int cond_index;
	  return_state r;
	  
	  lexer.putback(); // go back to top of loop
	  cond_index = lexer.index; //save top of loop location
	  token = lexer.get_token(); // read in "while" token again
	  
      ExpressionEvaluator eval = new ExpressionEvaluator(this);
	  cond = eval.eval_exp(); //evaluate the conditional statement
	  
	  if(cond.value.doubleValue()!=0){  // if any bit is not 0
		  r =interp_block(block_type.CONDITIONAL);  // execute loop
		  if(r==return_state.FUNC_RETURN)
			  return;
	  }
	  else{
		  find_eob(); //find the end of the loop
		  return;
	  }
	  
	  lexer.index = cond_index; // loop back to top
  }
  
  void exec_if(){
	  var_type cond;
	  return_state r;
	  ExpressionEvaluator eval = new ExpressionEvaluator(this);
	  cond = eval.eval_exp(); //evaluate the conditional statement
	  
	  if(cond.value.doubleValue()!=0){  // if any bit is not 0
		  r = interp_block(block_type.CONDITIONAL);  // execute block
		  if(r == return_state.FUNC_RETURN)
			  return;
		  
		  token = lexer.get_token();
		  if(token.key==keyword.ELSE){
			  token = lexer.get_token();
			  if(!token.value.equals("{")){
				  sntx_err(/*EXPECTING {*/);
			  }
			  find_eob();
		  }
		  else{
			  lexer.putback();
		  }
	  }
	  else{ //skip around block, check for else
		  find_eob(); //find the end of the loop
		  token = lexer.get_token();
		  
		  if(token.key != keyword.ELSE){
			  lexer.putback();
		  }
		  else{
			  r = interp_block(block_type.CONDITIONAL);
			  	if(r == return_state.FUNC_RETURN)
			  		return;
		  }
	  }
	  
  }
  
  void exec_do(){
	  var_type cond;
	  return_state r;
	  int do_index;
	  
	  lexer.putback(); // go back to top of loop
	  do_index = lexer.index; //save top of loop location
	  token = lexer.get_token(); // read in "do" token again
	  r = interp_block(block_type.CONDITIONAL); //interpret loop
	  if(r == return_state.FUNC_RETURN)
		  return;
	  
	  token = lexer.get_token();
	  if(token.key!=keyword.WHILE) sntx_err(/*while expected*/);
	  
	  ExpressionEvaluator eval = new ExpressionEvaluator(this);
	  cond = eval.eval_exp(); //evaluate the conditional statement
	  
	  if(cond.value.doubleValue()!=0)  // if any bit is not 0
		  lexer.index = do_index; //loop back
  }
  
  void find_eob(){
	  int brace_count = 1;
	  
	  //TODO check this, next line isn't needed
	  token = lexer.get_token();
	  
	  do{
		  token = lexer.get_token();
		  if(token.value.charAt(0) == '{')
			  brace_count++;
		  else if(token.value.charAt(0) == '}')
			  brace_count--;
		  
	  } while(brace_count>0);
	  
  }
  
  // Return index of internal library function or -1 if not found.
  public int internal_func(String s)
  {
  //TODO
  //for(int i=0; intern_func[i].f_name[0]; i++) {
  //if(!strcmp(intern_func[i].f_name, s)) return i;
  //}
  return -1;
  }
  
  /* Find the location of all functions in the program
  and store global variables. */
  // TODO: I should do something about functions as parameters, which may have functions as parameters, recursive..
  // TODO: add ability to have default parameters...
  // TODO: add ability to have definition and implementation for functions
  public void prescan()
  {
	  int oldIndex = lexer.index;
	  int tempIndex;
	  String temp;
	  keyword datatype;
	  int brace = 0; //When 0, this var tells us that current source position is outside of a function
	  func_index = 0;
	  
	  do {
		  while(brace>0 && token.key!=keyword.FINISHED) { //skip code inside functions
			  //index = 205;
			  token = lexer.get_token();
			  if(token.value.equals("{"))  brace++;
			  if(token.value.equals("}"))  brace--;
		  }
		  
		  tempIndex = lexer.index;
		  token = lexer.get_token();
		  //this token is var type or function type?
		  if(token.key==keyword.INT || token.key==keyword.CHAR || 
				  token.key==keyword.SHORT || token.key==keyword.BOOL ||
				  token.key==keyword.FLOAT || token.key==keyword.DOUBLE) {
			  datatype = token.key; //save data type
			  token = lexer.get_token();
			  if(token.type == token_type.IDENTIFIER) {
				  temp = new String(token.value);
				  token = lexer.get_token();
				  if(token.value.charAt(0) != '(') { //must be a global var
					  lexer.index = tempIndex; //return to start of declaration
					  decl_global();
				  }
				  else{ //must be a function
					  func_table[func_index] = new func_type();
					  func_table[func_index].ret_type = datatype;
					  func_table[func_index].func_name = new String(temp);
					  func_table[func_index].params = get_params();
					  func_table[func_index].location = lexer.index;
					  func_index++;
					  // now at opening curly brace of function
				  }
			  }
		  }
		  else if(token.value.equals("{"))  brace++;
		  else if(token.key!=keyword.FINISHED) sntx_err(/*unkown data type or command in prescan */);
	  } while(token.key!=keyword.FINISHED);
	  lexer.index = oldIndex;
  }
  
  /* calls the function whose name is in token, 
     index should be after the function name (at the open parenthesis, 
     before the arguments to the function)*/
  var_type call(String func_name){
	  int loc, temp;
	  int lvartemp;
	  ret_value = null;
	  ArrayList<var_type> args, params;
	  args = get_args();
	  int func_index;
	  
	  //find function
	  func_index = find_func(func_name,args);
	  
	  // set start of function
	  loc = func_table[func_index].location;
	  if(loc < 0){
		  sntx_err(/*FUNC_UNDEF*/);
	  }
	  else {
		  //TODO: THIS DOESN'T PROPERLY CHECK TO MAKE SURE ARGS MATCH
		  lvartemp = lvartos; //save local var stack index
		  temp = lexer.index; //save return location
		  func_push(lvartemp); //save local var stack index
		  lexer.index = loc; //reset prog to start of function
		  params = func_table[func_index].params; //get set of params
		  putParamsOnStack(args,params);
		  interp_block(block_type.FUNCTION); //run the function
		  lexer.index = temp; //reset the program index
		  lvartos = func_pop();
	  }
	  
	  return ret_value;
  }
  
  void putParamsOnStack(ArrayList<var_type> args, ArrayList<var_type> params){

	  if(args.size()!=params.size()){
		  sntx_err(/*BAD MATCH, more of a bug than a syntax error if this happens*/);
	  }
	  for(int i=0;i<params.size();i++){
		  var_type v = new var_type();
		  v.v_type = params.get(0).v_type;
		  v.var_name = params.get(0).var_name;
		  v.assignVal(args.get(0));
		  local_push(v);
	  }  
  }



  // get arguments from function call
  ArrayList<var_type> get_args(){
	  var_type value; 
	  ArrayList<var_type> args = new ArrayList<var_type>();
	  
	  token = lexer.get_token();
	  if(token.value.charAt(0) != '(') sntx_err(/*PAREN_EXPECTED*/);

	  //check if the function has args
	  token = lexer.get_token();
	  if(!token.value.equals(")")){
		  lexer.putback();
		  //process comma separated list of values
		  do{
			ExpressionEvaluator eval = new ExpressionEvaluator(this);
			value = eval.eval_exp(); 
			args.add(value); // save value temporarily
			token = lexer.get_token();
		  } while(token.value.charAt(0) == ',');
		  if(!token.value.equals(")"))
			  sntx_err(/*paren expected*/);
	  }
	  return args;
  }
  
  ArrayList<var_type> get_params(){
	  var_type p;
	  ArrayList<var_type> params = new ArrayList<var_type>();
	  
	  do { //process comma separated list of params
		  token = lexer.get_token();
		  if(token.value.charAt(0)!=')') {
			  p = new var_type();
			  if(token.key != keyword.INT && token.key != keyword.CHAR && 
					  token.key != keyword.FLOAT && token.key != keyword.DOUBLE && 
					  token.key != keyword.BOOL && token.key != keyword.SHORT)
				  sntx_err(/*TYPE_EXPECTED*/);
			  
			  p.v_type = token.key;
			  
			  token = lexer.get_token();
			  if(token.type != token_type.IDENTIFIER){
				 sntx_err(/*IDENTIFIER_EXPECTED*/);
			  }
			  p.var_name = token.value;
			  params.add(p);
			  
			  token = lexer.get_token();
		  }
		  else break;
	  } while(token.value.charAt(0) == ',');
	  if(!token.value.equals(")")) sntx_err(/*PAREN_EXPECTED*/);
	  
	  return params;
  }
  
  
  //return from a function. sets ret_value to the returned value
  void func_ret(){
	  var_type value;
	  //get return value (if any)
	  ExpressionEvaluator eval = new ExpressionEvaluator(this);
	  value = eval.eval_exp();
	  //TODO function should change return value to the correct type 
	  //TODO need to do something about void functions
	  
	  ret_value = value;  
  }
  
  
  int func_pop(){
	  functos--;
	  if(functos<0) sntx_err(/*SOME ERROR*/);
	  return call_stack[functos];  
  }
  
  void func_push(int i){
	  if(functos>=NUM_FUNC)
		  sntx_err(/*SOME ERROR*/);
	  call_stack[functos] = i;
	  functos++;
  }
  
  boolean isUserFunc(String name){
	  int i;
	  for(i=0; i < func_index; i++)
		  if(name.equals(func_table[i].func_name))
			  return true;
	  
	  return false;	  
	  
  }
  
  
  ArrayList<Integer> find_func_indexes(String name,  ArrayList<var_type> args){
	  ArrayList<Integer> indexes = new ArrayList<Integer>();
	  for(int i=0; i < func_index; i++)
		  if(name.equals(func_table[i].func_name) && args.size()==func_table[i].params.size() )
			  indexes.add(i);
	  
	  return indexes;
  }
  
  // returns index of function in func_table
  int find_func(String name, ArrayList<var_type> args){
	  ArrayList<Integer> indexes = find_func_indexes(name, args);

	  // remove possible matches that we can't convert args to
	  for(int j=0;j<indexes.size();++j){
		  for(int k=0; k<args.size(); ++k){
			  var_type arg = args.get(k);
			  var_type param = func_table[indexes.get(j)].params.get(k);
			  if(!arg.canConvertTo(param.v_type)){
				  indexes.remove(j);
				  j--;
			  }
		  }  
	  }
	  
	  if(indexes.size()==0){ // no matches\
		  sntx_err(/*No matching function*/);
		  return -1;
	  }
	  if(indexes.size()==1) // single match
		  return indexes.get(0);
		  
	  // Try to disambiguate overloaded function
	  // find best match
	  boolean bestMatches[] = new boolean[args.size()];
	  boolean matches[] = new boolean[args.size()];
	  int bestMatch = -1;
	  int bestNumMatches = -1;
	  
	  // to do this, first determine best match
	  for(int j=0;j<indexes.size();++j){
		  int numMatches = 0;
		  
		  for(int k=0; k<args.size(); ++k){
			  var_type arg = args.get(k);
			  var_type param = func_table[indexes.get(j)].params.get(k);
			  if(arg.v_type==param.v_type){
				  numMatches++;
				  matches[k] = true;
			  }
			  else{
				  matches[k] = false;
			  }
		  }
		  
		  if(numMatches > bestNumMatches ){
			  bestMatches = matches;
			  bestNumMatches = numMatches;
			  bestMatch = j;
		  }
	  }
	  
	  if(bestNumMatches == 0){ //ambiguous
		  sntx_err(/**/);
		  return -1;
	  }
	  
	  
	  // now make sure best is not ambiguous
	  for(int j=0;j<indexes.size();++j){
		  if(j==bestMatch) continue;
		  for(int k=0; k<args.size(); ++k){
			  var_type arg = args.get(k);
			  var_type param = func_table[indexes.get(j)].params.get(k);
			  if(arg.v_type==param.v_type && !bestMatches[j]){ //this implies ambiguous function call
				  sntx_err(/*ambiguous call*/);
				  return -1;
			  }
		  }
	  }
	  
	  
	  return indexes.get(bestMatch);
  }
  
  public class commands {
		public commands(String c, keyword t){
			command = c;
			tok = t;
		}
	public String command;
	public keyword tok;
	}
  
  public class func_type {
	  public String func_name;
	  public keyword ret_type;
	  public int location;
	  public ArrayList<var_type> params;//TODO 
	  public boolean overloaded;
  }

  
  
}

