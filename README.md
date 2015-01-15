# Projekt SCZR: Komunikacja miejska

## Cel projektu:

Celem projektu jest stworzenie systemu czasu rzeczywistego symulującego obsługę pasażerów linii autobusowej.

## Założenia:

1. Na komunikację miejską składa się jedna linia autobusowa.
2. Elementy składowe linii:
  1. zajezdnia,
  2. pętla,
  3. autobusy,
  4. przystanki
3. W zajezdni stacjonuje N autobusów, które w odpowiedzi na zapotrzebowanie są kierowane na pętlę.
4. Autobus z pętli wyrusza w trasę i odwiedza po kolei M przystanków, po czym wraca na pętlę.
5. W przypadku braku zapotrzebowania autobus który powrócił z trasy na pętlę wraca do zajezdni.
6. Autobus mieści maksymalnie P pasażerów.
7. Pasażerowie w losowych momentach pojawiają się na losowych przystankach mając przyporządkowany losowy przystanek docelowy.
8. Pasażerowie tworzą na przystanku kolejkę FIFO.
9. Po zatrzymaniu się autobusu na przystanku najpierw opuszczają go pasażerowie, dla których jest to przystanek docelowy, a następnie wsiadają do niego kolejne osoby w miarę wolnych miejsc.
10. Jeśli autobus jest pełny i nikt na danym przystanku nie wysiada, przystanek jest pomijany.
11. Jeśli nikt na danym przystanku nie czeka i nie chce wysiadać, przystanek jest pomijany.
12. Symulacja przebiega w dwóch trybach:
  1. tryb ręczny:
    1. symulacja odbywa się krok po kroku,
    2. symulacja postępuje wskutek interakcji z graficznym interfejsem użytkownika,
  2. tryb automatyczny - po uruchomieniu, symulacja przebiega płynnie do momentu zatrzymania.
13. Przymujemy rozkład jednostajny.

## Zadania do wykonania:

Patrz [#14](https://github.com/vanqyard/SZCZR/issues/14).
