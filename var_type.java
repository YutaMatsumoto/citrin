
  public class var_type{
	  
	  public String var_name;
	  public keyword v_type;
	  public Number value;
	  public static final String EQ = "==";
	  public static final String LT = "<";	
	  public static final String LE = "<=";
	  public static final String GT = ">";
	  public static final String GE = ">=";
	  public static final String NE = "!=";
	  
	  
	  var_type(){
		  var_name = null;
		  v_type = null;
	  }
	  var_type(var_type rhs){
		  var_name = rhs.var_name;
		  v_type = rhs.v_type;
		  value = rhs.value;
	  }
	  
	  private keyword getBinaryOpReturnType(keyword type1, keyword type2) {
		  if(type1 == keyword.DOUBLE || type2 == keyword.DOUBLE)
			  return keyword.DOUBLE;
		  else if(type1 == keyword.FLOAT || type2 == keyword.FLOAT)
			  return keyword.FLOAT;
		  else if(type1 == keyword.INT || type2 == keyword.INT)
			  return keyword.INT;
		  else if(type1 == keyword.SHORT || type2 == keyword.SHORT)
			  return keyword.SHORT;
		  else if(type1 == keyword.CHAR || type2 == keyword.CHAR)
			  return keyword.INT;
		  else if(type1 == keyword.BOOL || type2 == keyword.BOOL)
			  return keyword.INT;
		  else{
			  sntx_err(/*operations on these types not defined*/);
			  return null;
		  }
	  }
	  
	  var_type add(var_type rhs){
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
			  
			  if(returnType == keyword.DOUBLE){
				  v.v_type = keyword.DOUBLE;
				  v.value = value.doubleValue()+rhs.value.doubleValue();
			  }
			  else if(returnType == keyword.FLOAT){
				  v.v_type = keyword.FLOAT;
				  v.value = value.floatValue()+rhs.value.floatValue();
			  }
			  else if(returnType == keyword.INT){
				  v.v_type = keyword.INT;
				  v.value = value.intValue()+rhs.value.intValue();
			  }
			  else if(returnType == keyword.SHORT){
				  v.v_type = keyword.SHORT;
				  v.value = value.shortValue()+rhs.value.shortValue();
			  }
		  }
		  return v;
	  }
	  

	var_type sub(var_type rhs){
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
			  if(returnType == keyword.DOUBLE){
				  v.v_type = keyword.DOUBLE;
				  v.value = value.doubleValue()-rhs.value.doubleValue();
			  }
			  else if(returnType == keyword.FLOAT){
				  v.v_type = keyword.FLOAT;
				  v.value = value.floatValue()-rhs.value.floatValue();
			  }
			  else if(returnType == keyword.INT){
				  v.v_type = keyword.INT;
				  v.value = value.intValue()-rhs.value.intValue();
			  }
			  else if(returnType == keyword.SHORT){
				  v.v_type = keyword.SHORT;
				  v.value = value.shortValue()-rhs.value.shortValue();
			  }
		  }
		  return v;
	  }
	  
	  var_type mul(var_type rhs){
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
			  if(returnType == keyword.DOUBLE){
				  v.v_type = keyword.DOUBLE;
				  v.value = value.doubleValue()*rhs.value.doubleValue();
			  }
			  else if(returnType == keyword.FLOAT){
				  v.v_type = keyword.FLOAT;
				  v.value = value.floatValue()*rhs.value.floatValue();
			  }
			  else if(returnType == keyword.INT){
				  v.v_type = keyword.INT;
				  v.value = value.intValue()*rhs.value.intValue();
			  }
			  else if(returnType == keyword.SHORT){
				  v.v_type = keyword.SHORT;
				  v.value = value.shortValue()*rhs.value.shortValue();
			  }
		  }
		  return v;
	  }
	  
	  var_type div(var_type rhs){
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
			  if(rhs.value.doubleValue()==0)
				  run_err(/*div 0*/);
			  else if(returnType == keyword.DOUBLE){
				  v.v_type = keyword.DOUBLE;
				  v.value = value.doubleValue()/rhs.value.doubleValue();
			  }
			  else if(returnType == keyword.FLOAT){
				  v.v_type = keyword.FLOAT;
				  v.value = value.floatValue()/rhs.value.floatValue();
			  }
			  else if(returnType == keyword.INT){
				  v.v_type = keyword.INT;
				  v.value = value.intValue()/rhs.value.intValue();
			  }
			  else if(returnType == keyword.SHORT){
				  v.v_type = keyword.SHORT;
				  v.value = value.shortValue()/rhs.value.shortValue();
			  }
		  }
		  return v;
	  }
	  
	  var_type mod(var_type rhs){
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
			  if(returnType == keyword.DOUBLE || returnType == keyword.FLOAT){
				  sntx_err(/*mod uses integral types only*/);
			  }
			  if(rhs.value.intValue()==0)
				  run_err(/*div 0*/);
			  else if(returnType == keyword.INT){
				  v.v_type = keyword.INT;
				  v.value = value.intValue()%rhs.value.intValue();
			  }
			  else if(returnType == keyword.SHORT){
				  v.v_type = keyword.SHORT;
				  v.value = value.shortValue()%rhs.value.shortValue();
			  }
		  }
		  return v;
	  }
	  
	  var_type relationalOperator(var_type rhs, String op){
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
			  if(returnType == keyword.DOUBLE || returnType == keyword.FLOAT){
				  v.v_type = keyword.BOOL;
				  double val1 = value.doubleValue();
				  double val2 = rhs.value.doubleValue();
				  
				  if(op.equals(EQ)){
					  v.value = (val1 == val2)? 1 : 0;
				  }
				  else if(op.equals(LT)){
					  v.value = (val1 < val2)? 1 : 0;
				  }
				  else if(op.equals(LE)){
					  v.value = (val1 <= val2)? 1 : 0;
				  }
				  else if(op.equals(GT)){
					  v.value = (val1 > val2)? 1 : 0;
				  }
				  else if(op.equals(GE)){
					  v.value = (val1 >= val2)? 1 : 0;
				  }
				  else if(op.equals(NE)){
					  v.value = (val1 != val2)? 1 : 0;
				  }
				  
			  }
			  else if(returnType == keyword.INT || returnType == keyword.SHORT){
				  v.v_type = keyword.BOOL;
				  int val1 = value.intValue();
				  int val2 = rhs.value.intValue();
				  
				  if(op.equals(EQ)){
					  v.value = (val1 == val2)? 1 : 0;
				  }
				  else if(op.equals(LT)){
					  v.value = (val1 < val2)? 1 : 0;
				  }
				  else if(op.equals(LE)){
					  v.value = (val1 <= val2)? 1 : 0;
				  }
				  else if(op.equals(GT)){
					  v.value = (val1 > val2)? 1 : 0;
				  }
				  else if(op.equals(GE)){
					  v.value = (val1 >= val2)? 1 : 0;
				  }
				  else if(op.equals(NE)){
					  v.value = (val1 != val2)? 1 : 0;
				  }
			  }
		  }
		  return v;
	  }
	  
	  var_type unaryMinus(){
		  var_type v = new var_type();
		  if(isNumber()){
			  if(v_type == keyword.DOUBLE){
				  v.v_type = keyword.DOUBLE;
				  v.value = -value.doubleValue();
			  }
			  else if(v_type == keyword.INT || v_type == keyword.CHAR || v_type == keyword.BOOL){
				  v.v_type = keyword.INT;
				  v.value = -value.intValue();
			  }
			  else if(v_type == keyword.SHORT){
				  v.v_type = keyword.SHORT;
				  v.value = -value.shortValue();
			  }
		  }
		  return v;
	  }
	  
	  
	  void assignVal(var_type rhs){
		  if(v_type == keyword.INT) value = rhs.value.intValue();
		  if(v_type == keyword.SHORT) value = rhs.value.shortValue();		  
		  if(v_type == keyword.CHAR) value = (rhs.value.intValue()%256+256)%256;
		  if(v_type == keyword.BOOL){
			  if(rhs.v_type == keyword.FLOAT || rhs.v_type == keyword.DOUBLE)
				  value = (rhs.value.doubleValue() == 0)? 0: 1;
			  else if(rhs.v_type == keyword.INT || rhs.v_type == keyword.SHORT || 
					  rhs.v_type == keyword.CHAR || rhs.v_type == keyword.BOOL)
				  value = (rhs.value.intValue() == 0)? 0: 1;
		  }
		  if(v_type == keyword.FLOAT) value = rhs.value.floatValue();
		  if(v_type == keyword.DOUBLE) value = rhs.value.doubleValue();
	  }
	  
	  
	  boolean isNumber(){
		  return (v_type == keyword.INT || v_type == keyword.FLOAT || v_type == keyword.DOUBLE || 
				  v_type == keyword.CHAR || v_type == keyword.BOOL);
	  }
	  
	  boolean canConvertTo(keyword type){
		  //TODO temp function... needs to be changed if classes are added
		  return true;
	  }
	  
	  private void sntx_err() {
		    System.out.println("var_type error");
		    // TODO Auto-generated method stub

		  }
	  
	  private void run_err() {
		    System.out.println("var_type error");
		    // TODO Auto-generated method stub

		  }
	  
	  //public List<var_type> data; use this for classes?
  }
  
  /*public class primitive_var extends var_type {
	  public Number value;
  }*/ //maybe ill use this