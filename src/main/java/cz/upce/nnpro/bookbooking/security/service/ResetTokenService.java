package cz.upce.nnpro.bookbooking.security.service;

import cz.upce.nnpro.bookbooking.entity.ResetToken;
import cz.upce.nnpro.bookbooking.repository.ResetTokenRepository;
import cz.upce.nnpro.bookbooking.service.ServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ResetTokenService implements ServiceInterface<ResetToken> {

    private final ResetTokenRepository resetTokenRepository;

    @Override
    public List<ResetToken> getAll() {
        return resetTokenRepository.findAll();
    }

    @Override
    public ResetToken getById(Long id) {
        return resetTokenRepository.findById(id).orElse(null);
    }

    @Override
    public ResetToken create(ResetToken resetToken) {
        return resetTokenRepository.save(resetToken);
    }

    @Override
    public ResetToken update(ResetToken resetToken) {
        return resetTokenRepository.save(resetToken);
    }

    @Override
    public void deleteById(Long id) {
        resetTokenRepository.deleteById(id);
    }

    public ResetToken getByToken(String token) {return resetTokenRepository.findByToken(token).orElse(null);}

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredTokens() {
        resetTokenRepository.findAll().forEach(token -> {
            if (token.isExpired()) {
                resetTokenRepository.delete(token);
            }
        });
    }
}
