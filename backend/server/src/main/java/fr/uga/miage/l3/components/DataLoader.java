package fr.uga.miage.l3.components;

import fr.uga.miage.l3.dataTypes.Adresse;
import fr.uga.miage.l3.dataTypes.GeoPosition;
import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.models.*;
import fr.uga.miage.l3.enums.StatusEquipeLivreurs;
import fr.uga.miage.l3.repositories.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataLoader {
    private final EntrepotRepository entrepotRepository;
    private final LivreurRepository livreurRepository;
    private final EquipeLivreursRepository equipeLivreursRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final LigneRepository ligneRepository;
    private final CamionRepository camionRepository;
    private final TypeCamionRepository typeCamionRepository;
    private final PlanificateurRepository planificateurRepository;


    @PostConstruct
    public void loadData() throws Exception {
        Map<String, ClientEntity> clientMap = loadClients();
        Map<String, EntrepotEntity> entrepotMap = loadEntrepots();
        Map<String, PlanificateurEntity> planificateurMap = loadPlanificateurs(entrepotMap);
        Map<String, CamionEntity> camionMap = loadCamions(entrepotMap);
        loadTypeCamions(camionMap);
        Map<String, LivreurEntity> livreurMap = loadLivreurs(entrepotMap);
        loadEquipes(livreurMap);
        Map<String, ProduitEntity> produitMap = loadProduits();
        Map<String, CommandeEntity> commandeMap = loadCommandes(clientMap);
        loadLignes(produitMap, commandeMap);

    }

    private Map<String, PlanificateurEntity> loadPlanificateurs(Map<String, EntrepotEntity> entrepotMap) throws Exception {
        Map<String, PlanificateurEntity> planificateurMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/planificateurs.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1);
                if (tokens.length < 7) continue;

                PlanificateurEntity planificateur = new PlanificateurEntity();
                planificateur.setTrigramme(tokens[0].trim());
                planificateur.setPrenom(tokens[1].trim());
                planificateur.setNom(tokens[2].trim());
                planificateur.setTelephone(tokens[3].trim());
                planificateur.setPhoto(tokens[4].trim());
                planificateur.setEmail(tokens[5].trim());

                String entrepotNom = tokens[6].trim();
                EntrepotEntity entrepot = entrepotMap.get(entrepotNom);
                planificateur.setEntrepot(entrepot);

                planificateurRepository.save(planificateur);
                planificateurMap.put(planificateur.getTrigramme(), planificateur);
            }
        }
        return planificateurMap;
    }


    private void loadTypeCamions(Map<String, CamionEntity> camionMap) throws Exception {
        ClassPathResource resource = new ClassPathResource("data/type_camion.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (tokens.length < 4) continue;

                TypeCamionEntity typeCamion = new TypeCamionEntity();
                typeCamion.setNom(tokens[0].trim());
                typeCamion.setVolume(tokens[1].isEmpty() ? null : Double.valueOf(tokens[1].trim()));
                String prixStr = tokens[2].replace("€", "").replace(",", ".").trim();
                typeCamion.setPrixAuKm(prixStr.isEmpty() ? null : Double.valueOf(prixStr));
                typeCamion.setImage(tokens[3].trim());

                typeCamionRepository.save(typeCamion);

                // Lier les camions s'ils sont présents
                if (tokens.length > 4 && tokens[4] != null && !tokens[4].isEmpty()) {
                    String camionsStr = tokens[4].replace("\"", "");
                    String[] camionCodes = camionsStr.split(",");
                    Set<CamionEntity> camions = new HashSet<>();
                    for (String code : camionCodes) {
                        code = code.trim();
                        CamionEntity camion = camionMap.get(code);
                        if (camion != null) {
                            camion.setTypeCamion(typeCamion);
                            camions.add(camion);
                            camionRepository.save(camion);
                        }
                    }
                    typeCamion.setCamions(camions);
                    typeCamionRepository.save(typeCamion);
                }
            }
        }
    }

    private Map<String, CamionEntity> loadCamions(Map<String, EntrepotEntity> entrepotMap) throws Exception {
        Map<String, CamionEntity> camionMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/camions.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1);
                if (tokens.length < 5) continue;

                CamionEntity camion = new CamionEntity();
                camion.setCode(tokens[0].trim());
                camion.setImmatriculation(tokens[1].trim());


                String kilometrageStr = tokens[4].replace(" ", "").replace(",", "").trim();
                if (!kilometrageStr.isEmpty()) {
                    try {
                        camion.setKilometrage(Long.valueOf(kilometrageStr));
                    } catch (NumberFormatException e) {
                        camion.setKilometrage(null);
                    }
                } else {
                    camion.setKilometrage(null);
                }

                String entrepotNom = tokens[5].trim();
                EntrepotEntity entrepot = entrepotMap.get(entrepotNom);
                camion.setEntrepot(entrepot);

                camionRepository.save(camion);
                camionMap.put(camion.getCode(), camion);
            }
        }
        return camionMap;
    }


    private Map<String, CommandeEntity> loadCommandes(Map<String, ClientEntity> clientMap)
            throws Exception {
        Map<String, CommandeEntity> commandeMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/commandes.csv");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(),
                        StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(
                        ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (tokens.length < 7) {
                    continue;
                }

                CommandeEntity commande = new CommandeEntity();
                commande.setReference(tokens[0].trim());
                // initialisation du tdmThéorique
                commande.setTdmTheorique(0);

                if (tokens[1] != null && !tokens[1].isEmpty()) {
                    try {
                        commande.setEtat(EtatCommande.valueOf(tokens[1].toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        commande.setEtat(null);
                    }
                }

                if (tokens[2] != null && !tokens[2].isEmpty()) {
                    String dateStr = tokens[2].replace("\"", "").trim();
                    try {
                        commande.setDateDeCreation(
                                LocalDateTime.parse(dateStr, formatter));
                    } catch (Exception e) {
                        commande.setDateDeCreation(null);
                    }
                }

                if (tokens.length > 3
                        && tokens[3] != null
                        && !tokens[3].isEmpty()) {
                    try {
                        commande.setNote(Integer.valueOf(tokens[3].trim()));
                    } catch (NumberFormatException ignored) {}
                }

                if (tokens.length > 4
                        && tokens[4] != null
                        && !tokens[4].isEmpty()) {
                    commande.setCommentaire(tokens[4].trim());
                }

                String clientKey = tokens[5].trim();
                ClientEntity client = clientMap.get(clientKey);
                if (client == null) {
                    client = clientMap.values().stream()
                            .filter(c -> c.getEmail().trim()
                                    .equalsIgnoreCase(clientKey))
                            .findFirst()
                            .orElse(null);
                }
                if (client == null) {
                    System.out.println("WARNING: Client non trouvé pour la commande "
                            + commande.getReference()
                            + " (client: [" + clientKey + "])");
                }
                commande.setClient(client);

                commandeRepository.save(commande);
                commandeMap.put(commande.getReference(), commande);
            }
        }
        return commandeMap;
    }

    private void loadLignes(Map<String, ProduitEntity> produitMap,
                            Map<String, CommandeEntity> commandeMap)
            throws Exception {
        ClassPathResource resource = new ClassPathResource("data/lignes.csv");
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(resource.getInputStream(),
                        StandardCharsets.UTF_8))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",", -1);
                LigneEntity l = new LigneEntity();
                l.setReference(d[0].trim());

                CommandeEntity commande = commandeMap.get(d[1].trim());
                ProduitEntity produit = produitMap.get(d[2].trim());
                if (commande == null || produit == null) {
                    continue;
                }
                l.setCommande(commande);
                l.setProduit(produit);

                int qty;
                try {
                    qty = Integer.parseInt(d[3].trim());
                } catch (NumberFormatException e) {
                    qty = 0;
                }
                l.setQuantite(qty);

                boolean optAsm = Boolean.parseBoolean(d[4].trim());
                l.setOptionAssemblage(optAsm);

                double montant = produit.getPrix() * qty;
                l.setMontant(montant);

                // calcul tdmThéorique de la ligne
                int tdmLigne;
                if (optAsm && produit.getTdmTheorique() != null) {
                    tdmLigne = produit.getTdmTheorique() * qty;
                } else {
                    tdmLigne = 0;
                }
                l.setTdmTheorique(tdmLigne);

                try {
                    ligneRepository.save(l);
                } catch (Exception e) {
                    System.out.println("EREUUUUUUR en savegardant ligne "
                            + l.getReference() + ": " + e.getMessage());
                }

                // mise à jour du tdmThéorique de la commande
                int prev = commande.getTdmTheorique() != null
                        ? commande.getTdmTheorique()
                        : 0;
                commande.setTdmTheorique(prev + tdmLigne);
                commandeRepository.save(commande);
            }
        }
    }


    private Map<String, ProduitEntity> loadProduits() throws Exception {
        Map<String, ProduitEntity> produitMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/produits.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (tokens.length < 6) continue;

                ProduitEntity produit = new ProduitEntity();
                produit.setReference(tokens[0]);
                produit.setPhoto(tokens[1]);
                produit.setTitre(tokens[2]);
                produit.setDescription(tokens[3]);

                try {
                    String prixStr = tokens[4].replace("€", "").replace(",", ".").trim();
                    produit.setPrix(prixStr.isEmpty() ? 0.0 : Double.parseDouble(prixStr));
                } catch (Exception e) {
                    System.out.println("Error parsing prix for produit " + tokens[0] + ": " + e.getMessage());
                    produit.setPrix(0.0);
                }

                try {
                    produit.setOptionMontage(tokens[5].isEmpty() ? null : Boolean.valueOf(tokens[5]));
                } catch (Exception e) {
                    System.out.println("Error parsing optionMontage for produit " + tokens[0] + ": " + e.getMessage());
                    produit.setOptionMontage(null);
                }

                if (tokens.length > 6 && !tokens[6].isEmpty()) {
                    try {
                        produit.setTdmTheorique(Integer.valueOf(tokens[6]));
                    } catch (Exception e) {
                        System.out.println("Error parsing tdmTheorique for produit " + tokens[0] + ": " + e.getMessage());
                        produit.setTdmTheorique(null);
                    }
                } else {
                    produit.setTdmTheorique(null);
                }

                try {
                    produitRepository.save(produit);
                    produitMap.put(produit.getReference(), produit);
                } catch (Exception e) {
                    System.out.println("Error saving produit " + tokens[0] + ": " + e.getMessage());
                }
            }
        }
        return produitMap;
    }

    private Map<String, ClientEntity> loadClients() throws Exception {
        Map<String, ClientEntity> clientMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/clients.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (tokens.length < 9) continue;

                ClientEntity client = new ClientEntity();
                client.setCode(tokens[0]);
                client.setEmail(tokens[1]);
                client.setNom(tokens[2]);
                client.setPrenom(tokens[3]);

                // Adresse
                Adresse adr = new Adresse();
                adr.setAdresse(tokens[4]);
                adr.setVille(tokens[5]);
                adr.setCodePostal(tokens[6]);
                client.setAdresse(adr);

                // Coords
                GeoPosition coords = new GeoPosition();
                try {
                    coords.setLat(Double.parseDouble(tokens[7]));
                    coords.setLng(Double.parseDouble(tokens[8]));
                } catch (Exception e) {
                    coords.setLat(0.0);
                    coords.setLng(0.0);
                }
                client.setCoords(coords);

                if (tokens.length > 9 && tokens[9] != null && !tokens[9].isEmpty()) {
                    try {
                        client.setEtat(fr.uga.miage.l3.enums.EtatClient.valueOf(tokens[9].toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        client.setEtat(fr.uga.miage.l3.enums.EtatClient.LIVRABLE);
                    }
                } else {
                    client.setEtat(fr.uga.miage.l3.enums.EtatClient.LIVRABLE);
                }


                clientRepository.save(client);
                clientMap.put(client.getCode(), client);
            }
        }
        return clientMap;
    }

    private Map<String, EntrepotEntity> loadEntrepots() throws Exception {
        Map<String, EntrepotEntity> entrepotMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/entrepots.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {

                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (tokens.length < 8) continue;

                String nom = tokens[0];
                String lettre = tokens[1];
                String photo = tokens[2];
                String adresse = tokens[3];
                String codePostal = tokens[4];
                String ville = tokens[5];
                double latitude = Double.parseDouble(tokens[6]);
                double longitude = Double.parseDouble(tokens[7]);

                EntrepotEntity entrepot = new EntrepotEntity();
                entrepot.setNom(nom);
                entrepot.setLettre(lettre);
                entrepot.setPhoto(photo);

                fr.uga.miage.l3.dataTypes.Adresse adr = new fr.uga.miage.l3.dataTypes.Adresse();
                adr.setAdresse(adresse);
                adr.setCodePostal(codePostal);
                adr.setVille(ville);
                entrepot.setAdresse(adr);

                fr.uga.miage.l3.dataTypes.GeoPosition coords = new fr.uga.miage.l3.dataTypes.GeoPosition();
                coords.setLat(latitude);
                coords.setLng(longitude);
                entrepot.setCoords(coords);

                entrepotRepository.save(entrepot);
                entrepotMap.put(nom, entrepot);
            }
        }
        return entrepotMap;
    }

    private Map<String, LivreurEntity> loadLivreurs(Map<String, EntrepotEntity> entrepotMap) throws Exception {
        Map<String, LivreurEntity> livreurMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/livreurs.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length < 9) continue;

                LivreurEntity livreur = new LivreurEntity();
                livreur.setTrigramme(tokens[0]);
                livreur.setPrenom(tokens[1]);
                livreur.setNom(tokens[2]);
                livreur.setTelephone(tokens[3]);
                livreur.setPhoto(tokens[4]);
                livreur.setEmail(tokens[5]);
                livreur.setAffecte("oui".equalsIgnoreCase(tokens[6]));
                livreur.setAPermis("oui".equalsIgnoreCase(tokens[7]));

                String entrepotNom = tokens[8];
                EntrepotEntity entrepot = entrepotMap.get(entrepotNom);
                livreur.setEntrepot(entrepot);

                livreurRepository.save(livreur);
                livreurMap.put(livreur.getTrigramme(), livreur);
            }
        }
        return livreurMap;
    }

    private void loadEquipes(Map<String, LivreurEntity> livreurMap) throws Exception {
        ClassPathResource resource = new ClassPathResource("data/equipesdeLivreurs.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", 3);
                if (tokens.length < 3) continue;

                Integer idEquipe = Integer.valueOf(tokens[0]);
                StatusEquipeLivreurs status = StatusEquipeLivreurs.valueOf(tokens[1].toUpperCase());
                String livreursIds = tokens[2].replaceAll("\"", "");
                Set<LivreurEntity> livreurs = new HashSet<>();
                for (String trigram : livreursIds.split(",")) {
                    trigram = trigram.trim();
                    if (livreurMap.containsKey(trigram)) {
                        LivreurEntity livreur = livreurMap.get(trigram);
                        livreurs.add(livreur);
                    }
                }

                EquipeLivreursEntity equipe = new EquipeLivreursEntity();
                equipe.setIdEquipe(idEquipe);
                equipe.setStatus(status);
                equipe.setLivreurs(livreurs);

                equipeLivreursRepository.save(equipe);

                for (LivreurEntity livreur : livreurs) {
                    livreur.setEquipeLivreurs(equipe);
                    livreurRepository.save(livreur);
                }
            }
        }
    }
}