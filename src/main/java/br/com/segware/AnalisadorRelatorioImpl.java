package br.com.segware;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class AnalisadorRelatorioImpl implements IAnalisadorRelatorio {

    private final String FILE = "src/test/java/br/com/segware/relatorio.csv";
    private final String SPLIT = ",";
    private BufferedReader bufferedReader = null;

    public AnalisadorRelatorioImpl() throws FileNotFoundException {
        bufferedReader = new BufferedReader(new FileReader(FILE));
    }


    public Map<String, Integer> getTotalEventosCliente() {
        // String = client code
        // Integer = quantity events about client
        Map<String, Integer> mapTotalCustomerEvents = new HashMap<>();

        String line;

        try {
            if ((line = bufferedReader.readLine()) != null) {
                do {
                    String[] columns = line.split(SPLIT);
                    Integer quantity = mapTotalCustomerEvents.get(columns[1]);
                    int temp = (quantity == null ? 1 : quantity + 1);

                    mapTotalCustomerEvents.put(columns[1], temp);
                } while ((line = bufferedReader.readLine()) != null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeBufferedReader();
        }

        return mapTotalCustomerEvents;
    }


    public Map<String, Long> getTempoMedioAtendimentoAtendente() {
        // String = client code
        // Long = time attendant medio
        Map<String, Long> mapMedioTimeServiceFromAttendant = new HashMap<>();
        Map<String, List<Long>> mapAmountCallsAttendant = new HashMap<>();
        String line;

        try {
            if ((line = bufferedReader.readLine()) != null) {
                do {
                    String[] columns = line.split(SPLIT);
                    Long time = calculateTimeService(columns[4], columns[5]);

                    List<Long> temp = mapAmountCallsAttendant.get(columns[6]);
                    // verify with temp is instanced
                    List<Long> list = temp != null ? temp : new ArrayList<Long>();

                    list.add(time);
                    // add time to attendant
                    mapAmountCallsAttendant.put(columns[6], list);
                } while ((line = bufferedReader.readLine()) != null);
            }

            for (String attendant : mapAmountCallsAttendant.keySet()) {
                Long totalServiceTime = 0L;

                for (Long attendantServiceTime : mapAmountCallsAttendant.get(attendant)) {
                    // sum all serivce time to calculate average time
                    totalServiceTime += attendantServiceTime;
                }

                Long time = totalServiceTime / mapAmountCallsAttendant.get(attendant).size();
                mapMedioTimeServiceFromAttendant.put(attendant, time);
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            closeBufferedReader();
        }

        return mapMedioTimeServiceFromAttendant;
    }

    public List<Tipo> getTiposOrdenadosNumerosEventosDecrescente() {
        List<Tipo> orderlyListDescending = new ArrayList<>();
        Map<String, Integer> mapQuantityByType = new HashMap<>();

        String line;

        try {
            if ((line = bufferedReader.readLine()) != null) {
                do {
                    String[] columns = line.split(SPLIT);

                    // verify if contains event type to count quantity about the event
                    if (mapQuantityByType.containsKey(columns[3])) {
                        mapQuantityByType.put(columns[3], mapQuantityByType.get(columns[3]) + 1);
                        continue;
                    }

                    // if isn't type event, is add 1 to event
                    mapQuantityByType.put(columns[3], 1);
                } while ((line = bufferedReader.readLine()) != null);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeBufferedReader();
        }

        List list = new LinkedList<>(mapQuantityByType.entrySet());
        sortList(list);

        Iterator it = list.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            orderlyListDescending.add(Tipo.getValue((String) entry.getKey()));
        }

        return orderlyListDescending;
    }

    public List<Integer> getCodigoSequencialEventosDesarmeAposAlarme() {
        List<Integer> listCodigoSequencialEventosDesarmeAposAlarme = new ArrayList<>();
        String linha;
        String previousTime = "";

        try {
            if ((linha = bufferedReader.readLine()) != null) {
                do {
                    String[] columns = linha.split(SPLIT);

                    // It is alarming?
                    if (Tipo.getValue(columns[3]) == Tipo.ALARME) {
                        previousTime = columns[4];
                        // continue iteration without execute next if
                        continue;
                    }

                    // It is disarming and larger type than 5 minutes?
                    if (Tipo.getValue(columns[3]) == Tipo.DESARME && previousCalculateTimeAlarm(previousTime, columns[4])) {
                        listCodigoSequencialEventosDesarmeAposAlarme.add(new Integer(columns[0]));
                        // exit iteration
                        break;
                    }
                } while ((linha = bufferedReader.readLine()) != null);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            closeBufferedReader();
        }

        return listCodigoSequencialEventosDesarmeAposAlarme;
    }

    private Long calculateTimeService(String initialDate, String finalDate) throws ParseException {
        return calculateTime(initialDate, finalDate) / 1000;
    }

    private boolean previousCalculateTimeAlarm(String initialDate, String finalDate) throws ParseException {
        System.out.print(calculateTime(initialDate, finalDate));
        return calculateTime(initialDate, finalDate) < 300000;
    }

    private long calculateTime(String initialDate, String finalDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(finalDate).getTime() - dateFormat.parse(initialDate).getTime();
    }

    private void sortList(List list) {
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });
    }

    private void closeBufferedReader() {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

