package pe.gob.salud.tb.infrastructure.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import pe.gob.salud.tb.domain.model.calculo.*;
import pe.gob.salud.tb.domain.port.CalculosPort;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Implementación JPA nativa de cálculos/consultas.
 * Respeta filtros (ámbito, fechas y clasif) y, si no hay filtros, devuelve TODO.
 */
@Component
public class CalculosJpaAdapter implements CalculosPort {

  @PersistenceContext
  private EntityManager em;

  /** Columna de fecha real (personas) */
  private static final String DATE_COL = "p.\"fecha_atencion\"";

  /* ========= Mapa de columnas EXISTENTES (según tu tabla) ========= */
  private static final Map<String,String> COL = Map.ofEntries(
      Map.entry("con_tb",                           "fa.\"Con TB\""),
      Map.entry("sintomatico_respiratorio",        "fa.\"Sintomático respiratorio\""),
      Map.entry("con_terapia_preventiva_tb",       "fa.\"Con terapia preventiva de TB\""),
      Map.entry("con_prueba_tuberculina_positiva", "fa.\"Con prueba de tuberculina positiva\""),
      Map.entry("recibe_hemodialisis",             "fa.\"Recibe hemodiálisis\""),
      Map.entry("con_diabetes",                    "fa.\"Con diabetes\""),
      Map.entry("con_desnutricion",                "fa.\"Con Desnutrición\""),
      Map.entry("recibe_dialisis_peritoneal_renal","fa.\"Recibe diálisis peritoneal /renal\""),
      Map.entry("recibio_trasplante_organo",       "fa.\"Recibió un trasplante de órgano\""),
      Map.entry("contacto_personas_tb",            "fa.\"Contacto de personas con TB\""),
      Map.entry("privada_libertad",                "fa.\"Privada de libertad\""),
      Map.entry("vih",                             "fa.\"Con infección por el VIH\""),
      Map.entry("alcohol_forma_nociva",            "fa.\"Consume alcohol de forma nociva\""),
      Map.entry("fumador_tabaco",                  "fa.\"Fumador de tabaco\""),
      Map.entry("rx_torax_anormal",                "fa.\"Con radiografía de tórax anormal\""),
      Map.entry("hemoptisis",                      "fa.\"Hemoptisis\""),
      Map.entry("tos_actual",                      "fa.\"Tos actual\""),
      Map.entry("trabajador_salud",                "fa.\"Trabajador/a de salud\""),
      Map.entry("consumo_drogas",                  "fa.\"Consume drogas\""),
      Map.entry("vive_distritos_tx_alta_tb",       "fa.\"Persona que vive en distritos con transmisión alta de TB\""),
      Map.entry("personas_60_mas",                 "fa.\"Personas de 60 años o mayores\""),
      Map.entry("fiebre_actual",                   "fa.\"Fiebre actual\""),

      // de referencia
      Map.entry("renaes",                          "fa.\"renaes\""),
      Map.entry("ubigeo",                          "fa.\"ubigeo\""),
      Map.entry("sexo",                            "fa.\"Sexo\""),
      Map.entry("analfabetismo",                   "fa.\"Analfabetismo\""),
      Map.entry("pobreza",                         "fa.\"Pobreza\"")
  );

  /** Variables binarias (0/1) para “todas” en calidad (solo las que existen) */
  private static final Set<String> BINARY_KEYS = Set.of(
      "con_tb","sintomatico_respiratorio","con_terapia_preventiva_tb",
      "con_prueba_tuberculina_positiva","recibe_hemodialisis","con_diabetes",
      "con_desnutricion","recibe_dialisis_peritoneal_renal","recibio_trasplante_organo",
      "contacto_personas_tb","privada_libertad","vih","alcohol_forma_nociva",
      "fumador_tabaco","rx_torax_anormal","hemoptisis","tos_actual",
      "trabajador_salud","consumo_drogas",
      "vive_distritos_tx_alta_tb","personas_60_mas","fiebre_actual"
  );

  /** groupBy -> columna real */
  private static final Map<String,String> GROUP_COL = Map.of(
      "diris",     "e.diris",
      "provincia", "e.prove_eess",
      "distrito",  "e.dist_eess",
      "renaes",    "fa.\"renaes\""
  );

  private static boolean notBlank(String s){ return s != null && !s.isBlank(); }

  private static LocalDate toLocalDate(Object v) {
    if (v == null) return null;
    if (v instanceof LocalDate ld) return ld;
    if (v instanceof Date d) return d.toLocalDate();
    if (v instanceof Timestamp ts) return ts.toLocalDateTime().toLocalDate();
    if (v instanceof java.util.Date ud) return ud.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    return LocalDate.parse(v.toString().substring(0,10));
  }

  /* ==================== WHERE builders (ámbito/fechas/clasif) ==================== */

  private static String buildWhereAmbito(String diris, String provincia, String distrito, String renaes) {
    List<String> conds = new ArrayList<>();
    if (notBlank(diris))     conds.add("e.diris      = :diris");
    if (notBlank(provincia)) conds.add("e.prove_eess = :prov");
    if (notBlank(distrito))  conds.add("e.dist_eess  = :dist");
    if (notBlank(renaes))    conds.add("fa.renaes    = :ren");
    return String.join(" AND ", conds);
  }

  private static String buildWhereFechas(LocalDate desde, LocalDate hasta) {
    List<String> conds = new ArrayList<>();
    if (desde != null) conds.add(DATE_COL + " >= :desde");
    if (hasta != null) conds.add(DATE_COL + " <  :hastaPlus"); // exclusivo
    return String.join(" AND ", conds);
  }

  /** clasif = all | 1 | 0  (usa “Con TB”) */
  private static String buildWhereClasif(String clasif) {
    if (!notBlank(clasif) || "all".equalsIgnoreCase(clasif)) return "";
    if ("1".equals(clasif)) return COL.get("con_tb") + " = 1";
    if ("0".equals(clasif)) return COL.get("con_tb") + " = 0";
    return "";
  }

  private static Query bindAmbito(Query q, String diris, String provincia, String distrito, String renaes) {
    if (notBlank(diris))     q.setParameter("diris", diris);
    if (notBlank(provincia)) q.setParameter("prov",  provincia);
    if (notBlank(distrito))  q.setParameter("dist",  distrito);
    if (notBlank(renaes))    q.setParameter("ren",   renaes);
    return q;
  }

  private static Query bindFechas(Query q, LocalDate desde, LocalDate hasta) {
    if (desde != null) q.setParameter("desde", desde);
    if (hasta != null) q.setParameter("hastaPlus", hasta.plusDays(1));
    return q;
  }

  private static String whereFinal(String... parts) {
    List<String> xs = new ArrayList<>();
    for (String p: parts) if (notBlank(p)) xs.add(p);
    return xs.isEmpty() ? "" : ("WHERE " + String.join(" AND ", xs));
  }

  /* ==================== CALIDAD: plana ==================== */
  @Override
  public List<DataQualityMetric> dataQualityFactores(
      List<String> variables,
      String diris, String provincia, String distrito, String renaes,
      LocalDate desde, LocalDate hasta,
      String clasif
  ) {
    List<String> keys = (variables == null || variables.isEmpty())
        ? new ArrayList<>(BINARY_KEYS)
        : variables;

    List<String> cols = keys.stream()
        .map(String::toLowerCase).map(COL::get)
        .filter(Objects::nonNull).distinct().toList();
    if (cols.isEmpty()) return List.of();

    String ambito = buildWhereAmbito(diris, provincia, distrito, renaes);
    String fechas = buildWhereFechas(desde, hasta);
    String whClas = buildWhereClasif(clasif);
    String where  = whereFinal(ambito, fechas, whClas);

    List<DataQualityMetric> out = new ArrayList<>();
    for (String col : cols) {
      String sql = """
        SELECT COUNT(*),
               SUM(CASE WHEN %1$s IS NULL THEN 1 ELSE 0 END),
               SUM(CASE WHEN %1$s = 1     THEN 1 ELSE 0 END),
               SUM(CASE WHEN %1$s = 0     THEN 1 ELSE 0 END),
               SUM(CASE WHEN %1$s IS NOT NULL AND %1$s NOT IN (0,1) THEN 1 ELSE 0 END)
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.establecimientos e ON e.renaes = fa.renaes
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %2$s
      """.formatted(col, where);

      Query q = em.createNativeQuery(sql);
      bindAmbito(q, diris, provincia, distrito, renaes);
      bindFechas(q, desde, hasta);

      Object[] r = (Object[]) q.getSingleResult();
      long total    = ((Number) r[0]).longValue();
      long nulls    = ((Number) r[1]).longValue();
      long ones     = ((Number) r[2]).longValue();
      long zeros    = ((Number) r[3]).longValue();
      long invalids = ((Number) r[4]).longValue();

      out.add(new DataQualityMetric(prettyTokenFrom(col), total, nulls, zeros, ones, invalids));
    }
    return out;
  }

  /* ==================== CALIDAD: agrupada ==================== */
  @Override
  public List<DataQualityMetricGrouped> dataQualityFactoresGrouped(
      List<String> variables, String groupBy,
      String diris, String provincia, String distrito, String renaes,
      Integer limit, LocalDate desde, LocalDate hasta,
      String clasif
  ) {
    String groupExpr = GROUP_COL.get(groupBy);
    if (groupExpr == null) throw new IllegalArgumentException("groupBy inválido: " + groupBy);

    List<String> keys = (variables == null || variables.isEmpty())
        ? new ArrayList<>(BINARY_KEYS)
        : variables;

    List<String> cols = keys.stream()
        .map(String::toLowerCase).map(COL::get)
        .filter(Objects::nonNull).distinct().toList();
    if (cols.isEmpty()) return List.of();

    String ambito = buildWhereAmbito(diris, provincia, distrito, renaes);
    String fechas = buildWhereFechas(desde, hasta);
    String whClas = buildWhereClasif(clasif);
    String where  = whereFinal(ambito, fechas, whClas);

    List<DataQualityMetricGrouped> out = new ArrayList<>();
    for (String col : cols) {
      String sql = """
        SELECT %1$s,
               COUNT(*),
               SUM(CASE WHEN %2$s IS NULL THEN 1 ELSE 0 END),
               SUM(CASE WHEN %2$s = 1     THEN 1 ELSE 0 END),
               SUM(CASE WHEN %2$s = 0     THEN 1 ELSE 0 END),
               SUM(CASE WHEN %2$s IS NOT NULL AND %2$s NOT IN (0,1) THEN 1 ELSE 0 END)
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.establecimientos e ON e.renaes = fa.renaes
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %3$s
        GROUP BY %1$s
        ORDER BY %1$s
      """.formatted(groupExpr, col, where);

      Query q = em.createNativeQuery(sql);
      if (limit != null && limit > 0) q.setMaxResults(limit);
      bindAmbito(q, diris, provincia, distrito, renaes);
      bindFechas(q, desde, hasta);

      @SuppressWarnings("unchecked")
      List<Object[]> rows = q.getResultList();
      for (Object[] r : rows) {
        String grp   = Objects.toString(r[0], null);
        long total   = ((Number) r[1]).longValue();
        long nulls   = ((Number) r[2]).longValue();
        long ones    = ((Number) r[3]).longValue();
        long zeros   = ((Number) r[4]).longValue();
        long invalid = ((Number) r[5]).longValue();
        out.add(new DataQualityMetricGrouped(grp, prettyTokenFrom(col), total, nulls, zeros, ones, invalid));
      }
    }
    return out;
  }

  /* ==================== RESUMEN ==================== */
  @Override
  public TbResumen tbResumen(String diris, String provincia, String distrito, String renaes,
                             LocalDate desde, LocalDate hasta, String clasif) {

    String colTb  = COL.get("con_tb");
    String ambito = buildWhereAmbito(diris, provincia, distrito, renaes);
    String fechas = buildWhereFechas(desde, hasta);
    String where  = whereFinal(ambito, fechas);

    String qTotal = """
        SELECT COUNT(*)
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.establecimientos e ON e.renaes = fa.renaes
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %s
      """.formatted(where);

    long total = ((Number) bindFechas(bindAmbito(em.createNativeQuery(qTotal), diris, provincia, distrito, renaes), desde, hasta)
        .getSingleResult()).longValue();

    String qConf = """
        SELECT COALESCE(SUM(CASE WHEN %s = 1 THEN 1 ELSE 0 END),0)
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.establecimientos e ON e.renaes = fa.renaes
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %s
      """.formatted(colTb, where);

    long confirmados = ((Number) bindFechas(bindAmbito(em.createNativeQuery(qConf), diris, provincia, distrito, renaes), desde, hasta)
        .getSingleResult()).longValue();

    String qNoConf = """
        SELECT COALESCE(SUM(CASE WHEN %s = 0 THEN 1 ELSE 0 END),0)
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.establecimientos e ON e.renaes = fa.renaes
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %s
      """.formatted(colTb, where);

    long noConfirmados = ((Number) bindFechas(bindAmbito(em.createNativeQuery(qNoConf), diris, provincia, distrito, renaes), desde, hasta)
        .getSingleResult()).longValue();

    double pctConfirmados = (total == 0) ? 0.0 : (confirmados * 100.0) / total;

    String qTop = """
        SELECT fa.renaes,
               COALESCE(MAX(e.eess), fa.renaes) AS nombre,
               COUNT(*) AS total_eess,
               COALESCE(SUM(CASE WHEN %s = 1 THEN 1 ELSE 0 END),0) AS conf_eess
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.establecimientos e ON e.renaes = fa.renaes
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %s
        GROUP BY fa.renaes
        ORDER BY total_eess DESC
        LIMIT 1
      """.formatted(COL.get("con_tb"), where);

    @SuppressWarnings("unchecked")
    List<Object[]> topRows = bindFechas(bindAmbito(em.createNativeQuery(qTop), diris, provincia, distrito, renaes), desde, hasta)
        .getResultList();

    String topRen = null, topNom = null;
    long topTotal = 0L, topConf = 0L;
    if (!topRows.isEmpty()) {
      Object[] t = topRows.get(0);
      topRen   = Objects.toString(t[0], null);
      topNom   = Objects.toString(t[1], null);
      topTotal = ((Number) t[2]).longValue();
      topConf  = ((Number) t[3]).longValue();
    }

    return new TbResumen(total, confirmados, noConfirmados, pctConfirmados,
        topRen, topNom, topTotal, topConf);
  }

  /* ==================== TOP EESS ==================== */
  @Override
  public List<TbTopEess> tbTopEess(String diris, String provincia, String distrito, String renaes,
                                   int limit, LocalDate desde, LocalDate hasta, String clasif) {
    String colTb  = COL.get("con_tb");
    String ambito = buildWhereAmbito(diris, provincia, distrito, renaes);
    String fechas = buildWhereFechas(desde, hasta);
    String whClas = buildWhereClasif(clasif);
    String where  = whereFinal(ambito, fechas, whClas);

    String sql = """
      SELECT fa.renaes, COALESCE(MAX(e.eess), fa.renaes) AS nombre, COUNT(*) AS total,
             COALESCE(SUM(CASE WHEN %s = 1 THEN 1 ELSE 0 END),0) AS confirmados
      FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
      JOIN clinica.establecimientos e ON e.renaes = fa.renaes
      JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
      %s
      GROUP BY fa.renaes
      ORDER BY total DESC
      LIMIT :lim
    """.formatted(colTb, where);

    Query q = em.createNativeQuery(sql).setParameter("lim", limit);
    bindAmbito(q, diris, provincia, distrito, renaes);
    bindFechas(q, desde, hasta);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();
    List<TbTopEess> out = new ArrayList<>();
    for (Object[] r : rows) {
      out.add(new TbTopEess(
          Objects.toString(r[0], null),
          Objects.toString(r[1], null),
          ((Number) r[2]).longValue(),
          ((Number) r[3]).longValue()
      ));
    }
    return out;
  }

  /* ==================== PERSONAS (lista) ==================== */
  @Override
public PagedPersonas personasBuscar(Integer page, Integer size,
                                    String diris, String provincia, String distrito, String renaes,
                                    LocalDate desde, LocalDate hasta, String clasif) {
  int p = (page == null || page < 0) ? 0 : page;
  int s = (size == null || size <= 0) ? 10 : size;

  String ambito = buildWhereAmbito(diris, provincia, distrito, renaes);
  String fechas = buildWhereFechas(desde, hasta);
  String whClas = buildWhereClasif(clasif);
  String where  = whereFinal(ambito, fechas, whClas);

  String colTb = COL.get("con_tb");
  String clasifLabel = """
    CASE
      WHEN %s = 1 THEN 'Confirmado'
      WHEN %s = 0 THEN 'No confirmado'
      ELSE ''
    END
  """.formatted(colTb, colTb);

  String sql = """
    SELECT fa."id_persona",
           MAX(%s) AS ultima_fecha,
           COALESCE(MAX(e.diris), '')      AS diris,
           COALESCE(MAX(e.eess), '')       AS eess,
           COALESCE(MAX(e.dist_eess), '')  AS distrito,
           %s                              AS clasif_label,
           ''                              AS loc
    FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
    JOIN clinica.establecimientos e ON e.renaes = fa.renaes
    JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
    %s
    GROUP BY fa."id_persona", clasif_label
    ORDER BY ultima_fecha DESC
    OFFSET :off LIMIT :lim
  """.formatted(DATE_COL, clasifLabel, where);

  Query nq = em.createNativeQuery(sql)
      .setParameter("off", p * s)
      .setParameter("lim", s);
  bindAmbito(nq, diris, provincia, distrito, renaes);
  bindFechas(nq, desde, hasta);

  @SuppressWarnings("unchecked")
  List<Object[]> rows = nq.getResultList();
  List<PersonaRow> items = new ArrayList<>();
  for (Object[] r : rows) {
    items.add(new PersonaRow(
        Objects.toString(r[0], null),
        toLocalDate(r[1]),
        Objects.toString(r[2], null),
        Objects.toString(r[3], null),
        Objects.toString(r[4], null),
        Objects.toString(r[5], ""),  // "Confirmado" / "No confirmado"
        Objects.toString(r[6], null)
    ));
  }

  String sqlCount = """
    SELECT COUNT(DISTINCT fa."id_persona")
    FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
    JOIN clinica.establecimientos e ON e.renaes = fa.renaes
    JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
    %s
  """.formatted(where);

  Query cq = em.createNativeQuery(sqlCount);
  bindAmbito(cq, diris, provincia, distrito, renaes);
  bindFechas(cq, desde, hasta);
  long total = ((Number) cq.getSingleResult()).longValue();

  return new PagedPersonas(items, total, p, s);
}


  /* ==================== SERIE TEMPORAL (mensual/semanal) ==================== */
  @Override
  public List<SerieRow> tbSerie(String granularidad,
                                String diris, String provincia, String distrito, String renaes,
                                LocalDate desde, LocalDate hasta, String clasif) {

    String periodExpr = "mensual".equalsIgnoreCase(granularidad)
        ? "to_char(date_trunc('month', " + DATE_COL + "), 'YYYY-MM')"
        : "to_char(date_trunc('week',  " + DATE_COL + "), 'IYYY-\"W\"IW')"; // 2024-W39

    String colTb  = COL.get("con_tb");
    String ambito = buildWhereAmbito(diris, provincia, distrito, renaes);
    String fechas = buildWhereFechas(desde, hasta);
    String whClas = buildWhereClasif(clasif);
    String where  = whereFinal(ambito, fechas, whClas);

    String sql = """
      SELECT %s AS periodo,
             COALESCE(SUM(CASE WHEN %s = 1 THEN 1 ELSE 0 END),0) AS confirmados,
             COALESCE(SUM(CASE WHEN %s = 0 THEN 1 ELSE 0 END),0) AS no_confirmados
      FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
      JOIN clinica.establecimientos e ON e.renaes = fa.renaes
      JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
      %s
      GROUP BY periodo
      ORDER BY periodo
    """.formatted(periodExpr, colTb, colTb, where);

    Query q = em.createNativeQuery(sql);
    bindAmbito(q, diris, provincia, distrito, renaes);
    bindFechas(q, desde, hasta);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();
    List<SerieRow> out = new ArrayList<>();
    for (Object[] r : rows) {
      out.add(new SerieRow(
          Objects.toString(r[0], null),
          ((Number) r[1]).longValue(),
          ((Number) r[2]).longValue()
      ));
    }
    return out;
  }

  /* ==================== MAPA ==================== */
  @Override
  public List<MapPointEess> mapaEess(String diris, String provincia, String distrito, LocalDate desde, LocalDate hasta) {
    String ambito = buildWhereAmbito(diris, provincia, distrito, null);
    String fechas = buildWhereFechas(desde, hasta);
    String where  = whereFinal(ambito, fechas);

    String sql = """
      SELECT fa.renaes, COALESCE(MAX(e.eess), fa.renaes) AS eess,
             COALESCE(MAX(e.lat), NULL) AS lat,
             COALESCE(MAX(e.lon), NULL) AS lon,
             COUNT(*) AS total
      FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
      JOIN clinica.establecimientos e ON e.renaes = fa.renaes
      JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
      %s
      GROUP BY fa.renaes
    """.formatted(where);

    Query q = em.createNativeQuery(sql);
    bindAmbito(q, diris, provincia, distrito, null);
    bindFechas(q, desde, hasta);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();
    List<MapPointEess> out = new ArrayList<>();
    for (Object[] r : rows) {
      out.add(new MapPointEess(
          Objects.toString(r[0], null),
          Objects.toString(r[1], null),
          r[2] == null ? null : ((Number) r[2]).doubleValue(),
          r[3] == null ? null : ((Number) r[3]).doubleValue(),
          ((Number) r[4]).longValue()
      ));
    }
    return out;
  }

  @Override
  public List<MapChoroplethDistrito> mapaDistritos(String diris, LocalDate desde, LocalDate hasta) {
    String ambito = notBlank(diris) ? "e.diris = :diris" : "";
    String fechas = buildWhereFechas(desde, hasta);
    String where  = whereFinal(ambito, fechas);

    String sql = """
      SELECT e.dist_eess, COUNT(*)
      FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
      JOIN clinica.establecimientos e ON e.renaes = fa.renaes
      JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
      %s
      GROUP BY e.dist_eess
    """.formatted(where);

    Query q = em.createNativeQuery(sql);
    if (notBlank(diris)) q.setParameter("diris", diris);
    bindFechas(q, desde, hasta);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();
    List<MapChoroplethDistrito> out = new ArrayList<>();
    for (Object[] r : rows) {
      out.add(new MapChoroplethDistrito(
          Objects.toString(r[0], null),
          ((Number) r[1]).longValue()
      ));
    }
    return out;
  }

  /* ==================== Incidencia x100k ==================== */
  @Override
  public IncidenciaK tbIncidencia(String diris, String provincia, String distrito, String renaes,
                                  LocalDate desde, LocalDate hasta, String clasif) {

    String colTb  = COL.get("con_tb");
    String ambito = buildWhereAmbito(diris, provincia, distrito, renaes);
    String fechas = buildWhereFechas(desde, hasta);
    String whClas = buildWhereClasif(clasif);
    String where  = whereFinal(ambito, fechas, whClas);

    String qNum = """
      SELECT COALESCE(SUM(CASE WHEN %s = 1 THEN 1 ELSE 0 END),0)
      FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
      JOIN clinica.establecimientos e ON e.renaes = fa.renaes
      JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
      %s
    """.formatted(colTb, where);

    Query qn = em.createNativeQuery(qNum);
    bindAmbito(qn, diris, provincia, distrito, renaes);
    bindFechas(qn, desde, hasta);
    long confirmados = ((Number) qn.getSingleResult()).longValue();

    Long poblacion;
    try {
      String wherePob = buildWhereAmbito(diris, provincia, distrito, renaes);
      String wP = wherePob.isBlank() ? "" : "WHERE " + wherePob;
      String qPob = "SELECT COALESCE(SUM(pob),0) FROM clinica.poblacion pop " + wP;
      Query qp = em.createNativeQuery(qPob);
      bindAmbito(qp, diris, provincia, distrito, null);
      poblacion = ((Number) qp.getSingleResult()).longValue();
      if (poblacion == 0) throw new IllegalStateException("pob=0");
    } catch (Exception ignore) {
      String qDen = """
        SELECT COUNT(DISTINCT p."id_persona")
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.establecimientos e ON e.renaes = fa.renaes
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %s
      """.formatted(where);
      Query qd = em.createNativeQuery(qDen);
      bindAmbito(qd, diris, provincia, distrito, renaes);
      bindFechas(qd, desde, hasta);
      poblacion = ((Number) qd.getSingleResult()).longValue();
    }

    double tasa = (poblacion == 0) ? 0.0 : (confirmados * 100000.0) / poblacion;
    return new IncidenciaK(confirmados, poblacion, tasa);
  }

  /* ==================== Por edad/sexo ==================== */
  @Override
public List<EdadSexoRow> tbPorEdadSexo(String diris, String provincia, String distrito, String renaes,
                                       LocalDate desde, LocalDate hasta, String clasif) {
  final String colTb  = COL.get("con_tb");

  String ambito = buildWhereAmbito(diris, provincia, distrito, renaes);
  String fechas = buildWhereFechas(desde, hasta);
  // OJO: aquí NO aplicamos clasif, para poder devolver ambos conteos
  String where  = whereFinal(ambito, fechas);

  String sql = """
      SELECT *
      FROM (
        SELECT
          CASE
            WHEN p."edad" IS NULL THEN 'ND'
            WHEN p."edad" BETWEEN 0 AND 4   THEN '0-4'
            WHEN p."edad" BETWEEN 5 AND 14  THEN '5-14'
            WHEN p."edad" BETWEEN 15 AND 24 THEN '15-24'
            WHEN p."edad" BETWEEN 25 AND 34 THEN '25-34'
            WHEN p."edad" BETWEEN 35 AND 44 THEN '35-44'
            WHEN p."edad" BETWEEN 45 AND 59 THEN '45-59'
            ELSE '60+'
          END AS grupo_edad,
          CASE
            WHEN p."sexo" = 'M' THEN 'Masculino'
            WHEN p."sexo" = 'F' THEN 'Femenino'
            ELSE 'No determinado'
          END AS sexo_label,
          COALESCE(SUM(CASE WHEN %s = 1 THEN 1 ELSE 0 END),0) AS confirmados,
          COALESCE(SUM(CASE WHEN %s = 0 THEN 1 ELSE 0 END),0) AS no_confirmados
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.establecimientos e ON e.renaes = fa.renaes
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %s
        GROUP BY grupo_edad, sexo_label
      ) t
      ORDER BY
        CASE t.grupo_edad
          WHEN '0-4'  THEN 1
          WHEN '5-14' THEN 2
          WHEN '15-24' THEN 3
          WHEN '25-34' THEN 4
          WHEN '35-44' THEN 5
          WHEN '45-59' THEN 6
          WHEN '60+'  THEN 7
          ELSE 99
        END,
        t.sexo_label
      """.formatted(colTb, colTb, where);

  Query q = em.createNativeQuery(sql);
  bindAmbito(q, diris, provincia, distrito, renaes);
  bindFechas(q, desde, hasta);

  @SuppressWarnings("unchecked")
  List<Object[]> rows = q.getResultList();
  List<EdadSexoRow> out = new ArrayList<>();
  for (Object[] r : rows) {
    out.add(new EdadSexoRow(
        Objects.toString(r[0], "ND"),                // grupo_edad
        Objects.toString(r[1], "No determinado"),    // sexo_label
        ((Number) r[2]).longValue(),                 // confirmados
        ((Number) r[3]).longValue()                  // no_confirmados
    ));
  }
  return out;
}




  /* ==================== Repeticiones / recaídas ==================== */
  @Override
  public RepeticionesResumen tbRepeticiones(String diris, String provincia, String distrito, String renaes,
                                            LocalDate desde, LocalDate hasta, Integer limit, String clasif) {
    String ambito = buildWhereAmbito(diris, provincia, distrito, renaes);
    String fechas = buildWhereFechas(desde, hasta);
    String whClas = buildWhereClasif(clasif);
    String where  = whereFinal(ambito, fechas, whClas);

    String base = """
      FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
      JOIN clinica.establecimientos e ON e.renaes = fa.renaes
      JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
      %s
    """.formatted(where);

    String qTotal = "SELECT COUNT(*) FROM ( " +
        "SELECT fa.\"id_persona\", COUNT(*) AS veces " + base +
        " GROUP BY fa.\"id_persona\" HAVING COUNT(*) >= 2 ) t";

    Query qt = em.createNativeQuery(qTotal);
    bindAmbito(qt, diris, provincia, distrito, renaes);
    bindFechas(qt, desde, hasta);
    long totalRep = ((Number) qt.getSingleResult()).longValue();

    String qTop = "SELECT fa.\"id_persona\", COUNT(*) AS veces " + base +
        " GROUP BY fa.\"id_persona\" HAVING COUNT(*) >= 2 " +
        " ORDER BY veces DESC, fa.\"id_persona\"";

    Query q = em.createNativeQuery(qTop);
    if (limit != null && limit > 0) q.setMaxResults(limit);
    bindAmbito(q, diris, provincia, distrito, renaes);
    bindFechas(q, desde, hasta);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();
    List<RepeticionRow> list = new ArrayList<>();
    for (Object[] r : rows) {
      list.add(new RepeticionRow(
          Objects.toString(r[0], null),
          ((Number) r[1]).longValue()
      ));
    }
    return new RepeticionesResumen(totalRep, list);
  }

  /* =========== Calidad: registros problemáticos (para tabla/CSV) =========== */
  @Override
  public List<QualityIssueRow> dataQualityProblemas(List<String> variables,
                                                    String diris, String provincia, String distrito, String renaes,
                                                    LocalDate desde, LocalDate hasta, String clasif, Integer limit) {

    List<String> keys = (variables == null || variables.isEmpty())
        ? new ArrayList<>(BINARY_KEYS)
        : variables;

    List<String> cols = keys.stream()
        .map(String::toLowerCase).map(COL::get)
        .filter(Objects::nonNull).distinct().toList();
    if (cols.isEmpty()) return List.of();

    String ambito = buildWhereAmbito(diris, provincia, distrito, renaes);
    String fechas = buildWhereFechas(desde, hasta);
    String whClas = buildWhereClasif(clasif);
    String where  = whereFinal(ambito, fechas, whClas);

    List<QualityIssueRow> out = new ArrayList<>();
    for (String col : cols) {
      String qNull = """
        SELECT fa."id_persona" AS id_cita, %s AS campo, 'Valor nulo' AS problema
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.establecimientos e ON e.renaes = fa.renaes
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %s AND %s IS NULL
      """.formatted("'" + prettyTokenFrom(col) + "'", where, col);

      Query q1 = em.createNativeQuery(qNull);
      if (limit != null && limit > 0) q1.setMaxResults(limit);
      bindAmbito(q1, diris, provincia, distrito, renaes);
      bindFechas(q1, desde, hasta);

      for (Object r : q1.getResultList()) {
        Object[] a = (Object[]) r;
        out.add(new QualityIssueRow(Objects.toString(a[0], null), Objects.toString(a[1], null), Objects.toString(a[2], null)));
      }

      String qInv = """
        SELECT fa."id_persona" AS id_cita, %s AS campo, 'Valor inválido' AS problema
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.establecimientos e ON e.renaes = fa.renaes
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %s AND %s IS NOT NULL AND %s NOT IN (0,1)
      """.formatted("'" + prettyTokenFrom(col) + "'", where, col, col);

      Query q2 = em.createNativeQuery(qInv);
      if (limit != null && limit > 0) q2.setMaxResults(limit);
      bindAmbito(q2, diris, provincia, distrito, renaes);
      bindFechas(q2, desde, hasta);

      for (Object r : q2.getResultList()) {
        Object[] a = (Object[]) r;
        out.add(new QualityIssueRow(Objects.toString(a[0], null), Objects.toString(a[1], null), Objects.toString(a[2], null)));
      }
    }
    return out;
  }

  /* ==================== Persona – (no usar en tu UI si no lo necesitas) ==================== */
  @Override
  public List<PersonaVarResumen> personaResumen(String idPersona, List<String> variables,
                                                LocalDate desde, LocalDate hasta) {
    if (idPersona == null || idPersona.isBlank()) return List.of();
    if (variables == null || variables.isEmpty()) return List.of();

    List<String> cols = variables.stream()
        .map(String::toLowerCase).map(COL::get)
        .filter(Objects::nonNull).distinct().toList();
    if (cols.isEmpty()) return List.of();

    String fechas = buildWhereFechas(desde, hasta);
    String where = whereFinal("fa.\"id_persona\" = :pid", fechas);

    List<PersonaVarResumen> out = new ArrayList<>();
    for (String col : cols) {
      String sql = """
        SELECT COALESCE(SUM(CASE WHEN %s = 1 THEN 1 ELSE 0 END),0) AS pos,
               COALESCE(SUM(CASE WHEN %s = 0 THEN 1 ELSE 0 END),0) AS neg
        FROM clinica."HISMINSA_FACTORES_RIESGO_ATENCIONES" fa
        JOIN clinica.personas p ON p."id_persona" = fa."id_persona"
        %s
      """.formatted(col, col, where);

      Query q = em.createNativeQuery(sql).setParameter("pid", Integer.valueOf(idPersona));
      bindFechas(q, desde, hasta);

      Object[] r = (Object[]) q.getSingleResult();
      long pos = ((Number) r[0]).longValue();
      long neg = ((Number) r[1]).longValue();

      out.add(new PersonaVarResumen(prettyTokenFrom(col), pos, neg));
    }
    return out;
  }

  /* ==================== helpers ==================== */
  private static String prettyTokenFrom(String columnExpr) {
    for (Map.Entry<String, String> e : COL.entrySet()) {
      if (Objects.equals(e.getValue(), columnExpr)) return e.getKey();
    }
    return columnExpr;
  }
}
