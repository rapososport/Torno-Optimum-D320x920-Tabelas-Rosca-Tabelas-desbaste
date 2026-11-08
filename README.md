# Optimum D320 × 920 — projeto Android

Aplicação Android nativa em Kotlin + Jetpack Compose, em português e sem dependência de rede.

## Abrir
1. Instalar o Android Studio.
2. Abrir a pasta `OptimumD320x920_Android`.
3. Aceitar a sincronização Gradle.
4. Executar em emulador ou dispositivo Android.

## Estado desta versão
- Interface dos módulos Desbaste e Rosqueamento.
- Quadro A com os três seletores mecânicos e cálculo de RPM pelo VFD.
- Calculadora de RPM teórica a partir de diâmetro e velocidade de corte.
- Quadro B com tabela Norton de avanços convertida para mm/rotação e apresentada com três casas decimais (longitudinal / transversal).
- As combinações finais de roscas/engrenagens ainda necessitam de validação visual da tabela original.

## Nota importante
A tabela de rotações foi transcrita da fotografia fornecida e considerada calibrada a 50 Hz. Confirmar com tacómetro. A extrapolação até 70 Hz não representa uma autorização de funcionamento nessa frequência.


## Criar o APK com GitHub Actions (sem Android Studio)

1. Cria uma conta em https://github.com/ (se ainda não tiveres).
2. Cria um repositório novo, por exemplo `OptimumD320x920`.
3. Descomprime este ZIP no computador e envia para o repositório todos os ficheiros e pastas que estão dentro de `OptimumD320x920_Android` (incluindo a pasta oculta `.github`).
4. No GitHub, abre o separador **Actions**. O workflow **Criar APK Android** deverá iniciar automaticamente após o envio para a branch `main`. Também podes abrir o workflow e escolher **Run workflow**.
5. Quando terminar com o visto verde, abre a execução, desce até **Artifacts** e descarrega `OptimumD320x920-APK`.
6. Descomprime o artefacto ZIP. Dentro estará `app-debug.apk`, que podes transferir para o telemóvel e instalar.

O APK `debug` é adequado para testes. Para distribuir publicamente, deve ser criada uma versão `release` assinada com uma chave privada guardada em segurança; nunca publiques essa chave no repositório.
