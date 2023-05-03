{ Exemplo de Programa na linguagem L com erro de sintaxe }

integer n;
final MAX=10;

for (n=1;n<=MAX;n=n+1) begin
  write(n);
  if (n > 5) begin
    writeln(" maior que 5");
  end else begin
    writeln(" menor ou igual a 5");
  end
end
