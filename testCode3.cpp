int fact(double n){
return 0;
}

int fact(int n){
if(n<=1){return 1;}
return n*fact(n-1);
}

int main()
{
  int i = -50,m=i;

  i = fact(10);
  i = fact(10.0);

  return 0;
}
