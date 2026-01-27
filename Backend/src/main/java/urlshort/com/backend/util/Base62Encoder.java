package urlshort.com.backend.util;

//utility class pt conversie Base62
//folosit pt generarea codurilor scurte
public class Base62Encoder {

    //alfabetul Base62, nu folosesc String pt ca vreau performanta (acces direct la index (0(1))) si eficienta memorie (char[] e mai compact decat String)
    private static final char[] BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final int BASE = 62;

    //converteste un numar long in string Base62
    //Algoritm:
    //se imparte numarul la 62
    //restul = index in alfabet -> caracter
    //catul devine noul numar
    //se repeta pana catul devine 0
    //se citesc caracterele de la ultimul la primul
    //encode(12345)
    //12345 ÷ 62 = 199 rest 7 → '7'
    //199 ÷ 62 = 3 rest 13 → 'D'
    //3 ÷ 62 = 0 rest 3 → '3'
    //Rezultat: "3D7" (dar citim invers) -> "7D3"
    public static String encode(long number){
        if(number == 0){
            return String.valueOf(BASE62_ALPHABET[0]);
        }

        StringBuilder result = new StringBuilder();

        //conversie numar -> Base62
        while(number > 0){
            int remainder = (int) (number % BASE);
            result.append(BASE62_ALPHABET[remainder]);
            number /= BASE;
        }

        //resturile se citesc invers
        return result.reverse().toString();
    }

    //converteste un string Base62 in numar long
    //Algoritm:
    //pt fiecare caracter (de la stanga la dreapta)
    //gaseste indexul caracterului in alfabet
    //inmulteste cu 62^pozitie si aduna la total
    //decode("abc123")
    //a = 36, b = 37, c = 38, 1 = 1, 2 = 2, 3 = 3
    //3×62^0 + 2×62^1 + 1×62^2 + 38×62^3 + 37×62^4 + 36×62^5
    public static long decode(String base62String){
        if(base62String == null || base62String.isEmpty()){
            throw new IllegalArgumentException("String-ul Base62 nu poate fii null sau gol");
        }

        long result = 0;
        long power = 1;
        for(int i = base62String.length() - 1; i >= 0; i--){
            char c = base62String.charAt(i);
            int index = indexOf(c);

            if(index == -1){
                throw new IllegalArgumentException(
                        "Caracter invalid in string Base62: " + c
                );
            }

            result += index * power;
            power *= BASE;
        }

        return result;
    }

    //gaseste indexul unui caracter in alfabetul Base62
    //folosesc cautarea liniara simpla (O(62) = O(1))
    //pt alfabete mai mari, as folosi HashMap pt O(1) lookup
    private static int indexOf(char c){
        for(int i = 0;i < BASE62_ALPHABET.length; i++){
            if(BASE62_ALPHABET[i] == c){
                return i;
            }
        }
        return -1;
    }

    //generarea unui cod scurt random folosind Base62
    //se genereaza un numar random (folosind timestamp + random)
    //se converteste in Base62
    //ajusteaza lungimea (padding sau truncate)
    public static String generateRandomCode(int length){
        if(length <= 0){
            throw new IllegalArgumentException("Lungimea trebuie sa fie > 0");
        }

        //combin timestamp cu random pt unicitate
        long timestamp = System.currentTimeMillis();
        long random = (long) (Math.random() * 1000000);
        long combined = timestamp * 1000000 + random;

        String encoded = encode(combined);//convertire in Base62

        if(encoded.length() > length){
            return encoded.substring(encoded.length() - length);//truncate, se iau ultimele N caractere (mai random)
        } else if(encoded.length() < length){
            return String.format("%" + length + "s", encoded).replace(' ', '0');//padding: adaug 0 la inceput
        }

        return encoded;
    }
}
