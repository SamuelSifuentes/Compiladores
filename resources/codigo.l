{ Programa que calcula o fatorial de um numero inteiro na linguagem L

integer n, fatorial, i;

write("Digite um numero inteiro positivo: ");
readln(n);
 }
if (n < 0) begin
    writeln("Erro: numero invalido");
end else if (n == 0) begin
    fatorial = 1;
end else begin
    fatorial = 1;
    for (i = 1; i <= n; i = i + 1) begin
        fatorial = fatorial * i;
    end
end

writeln("O fatorial de ", n, " e ", fatorial);
{ Programa que le dois numeros inteiros e calcula a sua soma }

integer a, b, soma;

write("Digite o primeiro numero: ");
readln(a);

write("Digite o segundo numero: ");
readln(b);

soma = a + b;

writeln("A soma de ", a, " e ", b, " e igual a ", soma, ".");
