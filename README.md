# Loja de hardware de PC

API REST para o cadastro de clientes, categorias e peças de computador e para o registro de pedidos. Usa Java 21, Spring Boot 3.3.5, Maven e MySQL.

## Como executar

Inicie o MySQL e crie o banco da loja em uma conexão local pelo Workbench.

```sql
CREATE DATABASE IF NOT EXISTS loja_hardware;
```

Na pasta que contém o `pom.xml`, use um PowerShell com Maven no `Path`. Ajuste a pasta do JDK se ele estiver instalado em outro local.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$senhaMySql = Read-Host 'Senha do MySQL' -AsSecureString
$env:SPRING_DATASOURCE_PASSWORD = [System.Net.NetworkCredential]::new('', $senhaMySql).Password
mvn spring-boot:run
```

O usuário padrão da conexão é `root`. Para outro usuário, defina `SPRING_DATASOURCE_USERNAME` no mesmo terminal. A API responde em `http://localhost:8080`. Em caso de porta ocupada, defina outra com `$env:SERVER_PORT = '8081'` antes de executar o Maven.

## Recursos

Cada rota oferece `GET` para listar, `GET /{id}` para consultar, `POST` para cadastrar, `PUT /{id}` para atualizar e `DELETE /{id}` para excluir.

| Rota | Campos para POST e PUT |
| --- | --- |
| `/clientes` | `nome`, `email` |
| `/categorias` | `nome`, `descricao` opcional |
| `/produtos` | `nome`, `sku`, `preco`, `estoque` disponível, `categoriaId` |
| `/pedidos` | `clienteId`, `itens` com `produtoId` e `quantidade` |

Um pedido baixa o estoque. A API recusa quantidades acima do saldo disponível com HTTP `409`. A alteração do pedido aplica a diferença de quantidades, e a exclusão devolve as unidades. O total é a soma dos subtotais calculados com `BigDecimal`. Cada item guarda o preço unitário aplicado ao entrar no pedido.

## Demonstração pelo PowerShell

Com a API rodando, execute os cadastros a seguir em outro terminal. O produto ficará com três unidades em estoque após o pedido, e o total será 559,80.

```powershell
$api = 'http://localhost:8080'
$tipo = 'application/json; charset=utf-8'
$cliente = Invoke-RestMethod -Method Post -Uri "$api/clientes" -ContentType $tipo -Body (
    @{ nome = 'Cliente Demonstração'; email = 'cliente.demo@example.com' } | ConvertTo-Json
)
$categoria = Invoke-RestMethod -Method Post -Uri "$api/categorias" -ContentType $tipo -Body (
    @{ nome = 'Armazenamento'; descricao = 'SSDs e discos rígidos' } | ConvertTo-Json
)
$produto = Invoke-RestMethod -Method Post -Uri "$api/produtos" -ContentType $tipo -Body (
    @{ nome = 'SSD NVMe 1 TB'; sku = 'SSD-NVME-1TB'; preco = 279.90; estoque = 5; categoriaId = $categoria.id } | ConvertTo-Json
)
$pedido = Invoke-RestMethod -Method Post -Uri "$api/pedidos" -ContentType $tipo -Body (
    @{ clienteId = $cliente.id; itens = @(@{ produtoId = $produto.id; quantidade = 2 }) } | ConvertTo-Json -Depth 4
)
Invoke-RestMethod -Uri "$api/pedidos/$($pedido.id)" | ConvertTo-Json -Depth 4
Invoke-RestMethod -Uri "$api/produtos/$($produto.id)"
```

O exemplo pressupõe que o e-mail, o nome de categoria e o SKU ainda estão disponíveis para cadastro.