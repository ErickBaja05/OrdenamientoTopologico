package src;

import java.util.*;

public class Grafo
{
    public class Arista {
        public final String desde;
        public final String hasta;

        public Arista(String d, String h) {
            desde = d;
            hasta = h;
        }

        @Override
        public String toString() {
            return desde + " -> " + hasta;
        }
    }
    private final Map<String, List<String>> listaAdj; // lista de adyacencia
    private final Map<String, Integer> gradosEntrada; // grados de entrada

    public Grafo()
    {
        listaAdj = new HashMap<>();
        gradosEntrada = new HashMap<>();
    }
    public void agregarNodo(String nodo)
    {
        listaAdj.putIfAbsent(nodo, new ArrayList<>());
        gradosEntrada.putIfAbsent(nodo, 0);
    }

    public void agregarArista(String desde, String hasta)
    {
        listaAdj.computeIfAbsent(desde, k -> new ArrayList<>()).add(hasta);
        listaAdj.putIfAbsent(hasta, new ArrayList<>());

        gradosEntrada.putIfAbsent(desde, 0);
        gradosEntrada.put(hasta, gradosEntrada.getOrDefault(hasta, 0) + 1);
    }
    public List<Arista> obtenerAristas()
    {
        List<Arista> aristas = new ArrayList<>();

        for (String desde : listaAdj.keySet())
        {
            for (String hasta : listaAdj.get(desde))
            {
                aristas.add(new Arista(desde, hasta));
            }
        }

        return aristas;
    }

    public void resetearGrados()
    {
        // Reiniciar grados
        gradosEntrada.clear();

        // Inicializar en 0 para todos los nodos
        for (String nodo : listaAdj.keySet())
        {
            gradosEntrada.put(nodo, 0);
        }

        // Recalcular grados de entrada
        for (String desde : listaAdj.keySet())
        {
            for (String hasta : listaAdj.get(desde))
            {
                gradosEntrada.put(hasta, gradosEntrada.get(hasta) + 1);
            }
        }
    }

    public void imprimirListaAdyacencia()
    {
        for (String nodo : listaAdj.keySet())
        {
            List<String> vecinos = listaAdj.get(nodo);

            if (vecinos.isEmpty())
            {
                System.out.println(nodo + " -> (sin vecinos)");
            }
            else
            {
                System.out.println(nodo + " -> " + String.join(", ", vecinos));
            }
        }
    }


    public List<String> algoritmoKahn()
    {
        Queue<String> cola = new LinkedList<>();
        List<String> orden = new ArrayList<>();

        // Agregar todos los vértices con grado de entrada 0
        for (String node : gradosEntrada.keySet())
        {
            if (gradosEntrada.get(node) == 0) cola.add(node);
        }

        while (!cola.isEmpty())
        {
            String current = cola.poll();
            orden.add(current);

            for (String vecino : listaAdj.getOrDefault(current, new ArrayList<>()))
            {
                gradosEntrada.put(vecino, gradosEntrada.get(vecino) - 1);
                if (gradosEntrada.get(vecino) == 0) cola.add(vecino);
            }
        }
        // Verificar si hubo ciclo
        if (orden.size() != gradosEntrada.size())
        {
            System.out.println("Ciclo");
            return null;
        } else {
            return orden;
        }
    }
}
