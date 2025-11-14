package pe.gob.salud.tb.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pe.gob.salud.tb.domain.model.catalogo.Ambitos;
import pe.gob.salud.tb.domain.model.catalogo.Eess;
import pe.gob.salud.tb.domain.port.CatalogoReaderPort;
import pe.gob.salud.tb.infrastructure.repository.EstablecimientoRepository;

import java.util.List;

@Component
public class CatalogoJpaAdapter implements CatalogoReaderPort {

  private final EstablecimientoRepository repo;

  public CatalogoJpaAdapter(EstablecimientoRepository repo) {
    this.repo = repo;
  }

  @Override
  public List<Eess> findEess(String diris, String prov, String dist, String q, int limit) {
    var rows = repo.findEess(emptyToNull(diris), emptyToNull(prov), emptyToNull(dist), emptyToNull(q));
    int cap = (limit <= 0 ? 200 : limit);
    return rows.stream()
        .limit(cap)
        .map(r -> new Eess(
            r.getRenaes(),
            r.getEess(),
            /* si getLat()/getLon() devolvieran Number: 
               r.getLat()==null?null:((Number)r.getLat()).doubleValue(), */
            r.getLat(),
            r.getLon()
        ))
        .toList();
  }

  @Override
  public Ambitos loadAmbitos() {
    var diris = repo.findAllDiris();
    var provs = repo.findAllProvincias();
    var dists = repo.findAllDistritos();
    return new Ambitos(diris, provs, dists);
  }

  // ===== NUEVOS: “traer todo” =====
  @Override public List<String> listAllDiris()      { return repo.findAllDiris(); }
  @Override public List<String> listAllProvincias() { return repo.findAllProvincias(); }
  @Override public List<String> listAllDistritos()  { return repo.findAllDistritos(); }

  @Override
  public List<Eess> listAllEess(String q, int limit) {
    var rows = repo.findEess(null, null, null, emptyToNull(q));
    int cap = (limit <= 0 ? 200 : limit);
    return rows.stream()
        .limit(cap)
        .map(r -> new Eess(
            r.getRenaes(),
            r.getEess(),
            r.getLat(),
            r.getLon()
        ))
        .toList();
  }

  private static String emptyToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }
}
