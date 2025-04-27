import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Voto {
    private int id;
    private int votanteID;
    private int candidatoID;
    private String horavoto;  //hora que salio el voto

    private static int contadorID = 0;

    public Voto(int votanteID, int candidatoID) {
        this.id = contadorID++; //te da un ID unico y luego lo suma para el prox
        this.votanteID = votanteID;
        this.candidatoID = candidatoID;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:s");
        this.horavoto = LocalDateTime.now().format(formatter);
    }

    public int getId() {
        return id;
    }

    public int getVotanteID() {
        return votanteID;
    }

    public int getCandidatoID() {
        return candidatoID;
    }

    public String getHoravoto() {
        return horavoto;
    }
}

class Candidato {
    private int id;
    private String nombre;
    private String partido;
    private Queue<Voto> votosRecibido;

    public Candidato(int id, String nombre, String partido) {
        this.id = id;
        this.nombre = nombre;
        this.partido = partido;
        this.votosRecibido = new LinkedList<>();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public String getPartido() {
        return partido;
    }
    public Queue<Voto> getVotosRecibido() {
        return votosRecibido;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setPartido(String partido) {
        this.partido = partido;
    }
    public void agregarVoto(Voto voto) {
        this.votosRecibido.add(voto);
    }

    public static void prueba(String[] args){
        Candidato candidato1 = new Candidato(1,"Sonic","A");

        System.out.println("Candidato 1: " + candidato1);

        //creamos unos votos

        Voto votoejemplo = new Voto(101,1);

        candidato1.agregarVoto(votoejemplo);

        System.out.println("Candidato después de agregar 1 voto: " + candidato1);
        System.out.println("Cantidad de votos recibidos: " + candidato1.getVotosRecibido().size());
        System.out.println("Nombre del candidato: " + candidato1.getNombre());
    }
}

class Votante {
    private int id;
    private String nombre;
    private boolean yaVoto;

    public Votante(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.yaVoto = false;
    }
    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public boolean isYaVoto() {
        return yaVoto;
    }

    public void marcarComoVotado(){
        this.yaVoto = true;
    }

    public static void main(String[] args){
        Votante votante1 = new Votante(101, "El pepe");
        System.out.println("Votante creado: " + votante1);

        // Verificamos el estado inicial
        System.out.println("¿Ya votó? " + votante1.isYaVoto());

        // Marcamos al votante como que ya votó
        votante1.marcarComoVotado();
        System.out.println("Marcando como votado");

        // Verificamos el estado después de marcar
        System.out.println("¿Ya votó ahora? " + votante1.isYaVoto());

        // Verificamos getters
        System.out.println("ID del votante: " + votante1.getId());
        System.out.println("Nombre del votante: " + votante1.getNombre());
    }
}

class UrnaElectoral {
    private List<Candidato> listacandidatos;
    private Stack<Voto> historialVotos;
    private Queue<Voto> votosReportados;

    public UrnaElectoral() {
        this.listacandidatos = new LinkedList<>();
        this.historialVotos = new Stack<>();
        this.votosReportados = new LinkedList<>();
    }

    //funcion para añadir candidado a lista
    public void agregarCandidato(Candidato candidato) {
        this.listacandidatos.add(candidato);
    }

    //Verifica si ya votó
    public boolean verificarVotante(Votante votante) {
        return votante==null || votante.isYaVoto();
    }

    public boolean registrarVoto(Votante votante, int candidatoID) {
        if(verificarVotante(votante)){
            System.out.println("El votante con la ID: " +votante.getId() + "ya esta registrado o es invalido");
            return false;
        }

        Candidato candidatoSeleccionado = null;
        for (Candidato c : listacandidatos) {
            if(c.getId() == candidatoID){
                candidatoSeleccionado = c;
                break;
            }
        }

        if(candidatoSeleccionado==null){
            System.out.println("El candidato con la ID"+candidatoID +"no existe");
            return false;
        }

        //crear nuevo voto, asiganrlo a la fila/cola del candidato // cambiar estado del votante
        Voto nuevoVoto= new Voto(votante.getId(),candidatoID);
        candidatoSeleccionado.agregarVoto(nuevoVoto);
        historialVotos.push(nuevoVoto);
        votante.marcarComoVotado();

        System.out.println("Voto del votante: "+votante.getId()+"registrado para "+candidatoSeleccionado.getNombre());
        return true;
    }

    public boolean reportarVoto(int candidatoID, int idVoto) {
        Candidato candidato = null;
        for (Candidato c : listacandidatos) {
            if(c.getId() == candidatoID){
                candidato = c;
                break;
            }
        }
        if(candidato==null){
            return false;
        }

        Voto votoAReportar =null;
        Queue<Voto> tempQueue = new LinkedList<>();
        while (!candidato.getVotosRecibido().isEmpty()){
            Voto votoactual = candidato.getVotosRecibido().poll();
            if(votoactual.getId() == idVoto){
                votoAReportar = votoactual;
            }
            else{
                tempQueue.add(votoactual);
            }
        }

        candidato.getVotosRecibido().addAll(tempQueue);

        if(votoAReportar!=null){
            votosReportados.add(votoAReportar);
            System.out.println("Voto de ID " + idVoto + " del candidato " + candidatoID + " reportado exitosamente.");
            return true;
        }
        return false;
    }

    public Map<String,Integer> obtenerResultados() {
        Map<String,Integer> resultados = new HashMap<>();
        for (Candidato c: listacandidatos){
            resultados.put(c.getNombre(), c.getVotosRecibido().size());
        }
        return resultados;
    }


    //funcion para tener el historial de votos
    public Stack<Voto> getHistorialVotos() {
        return (Stack<Voto>) historialVotos.clone();
    }

    public Queue<Voto> getVotosReportados() {
        return new LinkedList<>(votosReportados);
    }

    public static void main(String[] args){
        UrnaElectoral urna = new UrnaElectoral();
        Candidato c1 = new Candidato(1, "Juan Perez", "Partido A");
        Candidato c2 = new Candidato(2, "Maria Garcia", "Partido B");

        // Agregamos los candidatos a la urna
        urna.agregarCandidato(c1);
        urna.agregarCandidato(c2);

        Votante v1 = new Votante(101, "El Pepe Ramirez");
        Votante v2 = new Votante(102, "Ana Lopez");

        // Simulamos algunas votaciones
        System.out.println("\n--- Simulando votaciones ---");
        urna.registrarVoto(v1, 1); // Pedro vota por Juan
        urna.registrarVoto(v2, 2); // Ana vota por Maria
        urna.registrarVoto(v1, 2); // Pedro intenta votar de nuevo (debería fallar)

        System.out.println("\n--- Resultados finales ---");
        Map<String, Integer> resultadosFinales = urna.obtenerResultados();
        resultadosFinales.forEach((nombre, votos) -> System.out.println(nombre + ": " + votos + " votos"));

        System.out.println("\n--- Historial de votos (orden inverso) ---");
        Stack<Voto> historial = urna.getHistorialVotos();
        Stack<Voto> historialCopy = (Stack<Voto>) historial.clone();
        while (!historialCopy.empty()) {
            System.out.println(historialCopy.pop());
        }

        // Simulamos reportar un voto
        System.out.println("\n agregando un voto");
        // Reportar el voto con ID 0 (el primer voto, de Pedro por Juan)
        urna.reportarVoto(1, 0);

        System.out.println("\nVotos reportados");
        Queue<Voto> reportados = urna.getVotosReportados();
        reportados.forEach(System.out::println);
    }






}