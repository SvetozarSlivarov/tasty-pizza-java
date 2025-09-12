package com.example.service;

import com.example.dao.DrinkDao;
import com.example.dao.impl.DrinkDaoImpl;
import com.example.dto.DrinkCreateRequest;
import com.example.dto.DrinkUpdateRequest;
import com.example.exception.BadRequestException;
import com.example.exception.NotFoundException;
import com.example.model.Drink;
import com.example.model.enums.UserRole;

import java.util.List;

public class DrinkService {

    private final DrinkDao drinkDao;

    public DrinkService() {
        this.drinkDao = new DrinkDaoImpl();
    }

    public DrinkService(DrinkDao drinkDao) {
        this.drinkDao = drinkDao;
    }

    public List<Drink> findAll(boolean availableOnly, UserRole role) {
        if (availableOnly || role != UserRole.ADMIN) {
            return drinkDao.findAvailable();
        }
        return drinkDao.findAll();
    }

    public Drink findById(int id) {
        Drink d = drinkDao.findById(id);
        if (d == null) throw new NotFoundException("drink_not_found");
        return d;
    }

    public Drink create(DrinkCreateRequest req) {
        if (req == null) throw new BadRequestException("invalid_request");
        if (req.name() == null || req.name().trim().isEmpty()) {
            throw new BadRequestException("drink_name_required");
        }
        if (req.price() == null) {
            throw new BadRequestException("drink_price_required");
        }

        Drink d = new Drink();
        d.setName(req.name().trim());
        d.setPrice(req.price());
        d.setAvailable(req.isAvailable() == null ? true : req.isAvailable());

        if (req.description() != null) {
            String desc = req.description().trim();
            d.setDescription(desc.isEmpty() ? null : desc);
        }

        Drink saved = drinkDao.save(d);
        if (saved == null) throw new BadRequestException("drink_create_failed");
        return saved;
    }

    public Drink update(int id, DrinkUpdateRequest req) {
        Drink existing = findById(id);

        if (req.name() != null) {
            String name = req.name().trim();
            if (name.isEmpty()) throw new BadRequestException("drink_name_empty");
            existing.setName(name);
        }
        if (req.description() != null) {
            String desc = req.description().trim();
            existing.setDescription(desc.isEmpty() ? null : desc);
        }
        if (req.price() != null) {
            existing.setPrice(req.price());
        }
        if (req.isAvailable() != null) {
            existing.setAvailable(req.isAvailable());
        }

        Drink updated = drinkDao.update(existing);
        if (updated == null) throw new BadRequestException("drink_update_failed");
        return updated;
    }

    public void delete(int id) {
        Drink deleted = drinkDao.delete(id);
        if (deleted == null) {
            throw new NotFoundException("drink_not_found");
        }
    }
}
