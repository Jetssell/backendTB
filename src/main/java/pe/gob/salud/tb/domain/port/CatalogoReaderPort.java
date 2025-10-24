package pe.gob.salud.tb.domain.port;

import pe.gob.salud.tb.domain.model.catalogo.Ambitos;
import pe.gob.salud.tb.domain.model.catalogo.Eess;
import java.util.List;

public interface CatalogoReaderPort {

  List<Eess> findEess(String diris, String prov, String dist, String q, int limit);

  Ambitos loadAmbitos();

  // NUEVOS (para “traer todo” sin cascada)
  List<String> listAllDiris();
  List<String> listAllProvincias();
  List<String> listAllDistritos();
  List<Eess>   listAllEess(String q, int limit);
}
