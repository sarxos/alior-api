# Alior API

API do systemu internetowego Alior Banku. API to powstało ponieważ, przynajmniej 
oficjalnie, Alior Bank nie zamierzają takowej funkcjonalności implementować 
(informacje taką uzyskałem na ich infolini oraz mailowo przez panel wiadomości).
Nie wnikam w przyczyny, może wydaje im się, że nie da im to profitów, ale gdyby
zmienili zdanie, to niech wiedzą, że bardzo chętnie pomogę im takie rozwiązanie
stworzyć :)

## UWAGA!

Każdy kto ma zamiar użyć to API do własnych celów musi mieć świadomość, że może to
być potencjalnie niezgodne z  regulaminem, który posiadacz konta Alior Banku
akceptuje przy podpisywaniu umowy. Nie daję ŻADNEJ gwarancji, że API będzie działało
poprawnie, oraz nie biorę absolutnie ŻADNEJ odpowiedzialność za ewentualne szkody 
powstałe w wyniku jego użycia. Jeśli chcesz go używać, proszę bardzo, ale wiedz, 
że robisz to **NA SWOJĄ WŁASNĄ ODPOWIEDZIALNOŚĆ!**

## Opis

Jest to bardzo prymitywne API napisane w celu uzyskania zautomatyzowanego dostępu do 
panelu klienta w systemie internetowym Alior Banku. API to działa w oparciu o framework
Selenium, który otwiera sesję w przeglądarce, następnie po prostu klika na co trzeba
wykonując po drodze ekstrakcję danych z warstwy widoku.

API jest w fazie developmentu, co oznacza ni mniej ni więcej, że nie będę naprawiał 
żadnych zgłoszonych bugów i nie będę implementował żadnych dodatkowych ficzerów, 
jednak nie bronię nikomu zrobić tego w swoim własnym zakresie - wystarczy sforkować
repozytorium. Każdemu chętnemu życzę powodzenia będąc jednocześnie otwartym na
zwrotne pull requesty.

## Ficzery

1. Pobranie listy rachunków pieniężnych.
2. Pobranie listy rachunków maklerskich.
 * Pobranie listy papierów wartościowych.
 * Pobranie listy aktywnych zleceń.
 * Modyfikacja aktywnego zlecenia.

## Przykłady

W poniższych przykładach należy zamienić ```<cid>``` na własny numer klienta, zaś
```<password>``` na hasło do systemu.

```java

public static void main2(String[] args) throws AliorClientException {
	AliorClient alior = new AliorClient(FirefoxDriver.class);
	alior.login("<cid>", "<password>");
	List<MoneyAccount> accounts = alior.getMoneyAccounts();
	for (MoneyAccount ma : accounts) {
		System.out.println(ma);
	}
	alior.close();
}
```

## Licencja

The MIT License (MIT)

Copyright (c) 2011 - 2013 Bartosz Firyn

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
