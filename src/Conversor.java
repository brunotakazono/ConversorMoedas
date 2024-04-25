import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Conversor {

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/6479d5f25b63149c1b73cf19/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean sair = false;

        while (!sair) {
            System.out.println("Selecione uma opção de conversão:");
            System.out.println("1. USD para ARS (Peso argentino)");
            System.out.println("2. ARS para USD");
            System.out.println("3. USD para BRL (Real brasileiro)");
            System.out.println("4. BRL para USD");
            System.out.println("5. JPY para BRL (Real brasileiro)");
            System.out.println("6. BRL para JPY");
            System.out.println("7. Sair");

            int opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    realizarConversao(scanner, "USD", "ARS");
                    break;
                case 2:
                    realizarConversao(scanner, "ARS", "USD");
                    break;
                case 3:
                    realizarConversao(scanner, "USD", "BRL");
                    break;
                case 4:
                    realizarConversao(scanner, "BRL", "USD");
                    break;
                case 5:
                    realizarConversao(scanner, "JPY", "BRL");
                    break;
                case 6:
                    realizarConversao(scanner, "BRL", "JPY");
                    break;
                case 7:
                    sair = true;
                    System.out.println("Obrigado por usar o conversor de moedas!");
                    break;
                default:
                    System.out.println("Opção inválida. Por favor, escolha novamente.");
            }
        }

        scanner.close();
    }

    public static void realizarConversao(Scanner scanner, String moedaOrigem, String moedaDestino) {
        System.out.println("Insira o valor em " + moedaOrigem + " a ser convertido para " + moedaDestino + ":");
        double valor = scanner.nextDouble();

        try {
            double taxaDeCambio = obterTaxaDeCambio(moedaOrigem, moedaDestino);
            double valorConvertido = valor * taxaDeCambio;
            System.out.println("O valor convertido é: " + valorConvertido + " " + moedaDestino);
        } catch (IOException | InterruptedException e) {
            System.out.println("Erro ao obter taxa de câmbio: " + e.getMessage());
        }
    }

    public static double obterTaxaDeCambio(String moedaOrigem, String moedaDestino) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + moedaOrigem))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Gson gson = new Gson();
            JsonObject taxaDeCambioJson = gson.fromJson(response.body(), JsonObject.class);
            JsonObject rates = taxaDeCambioJson.getAsJsonObject("conversion_rates");
            return rates.get(moedaDestino).getAsDouble();
        } else {
            throw new RuntimeException("Falha ao obter taxa de câmbio. Código de status: " + response.statusCode());
        }
    }
}
