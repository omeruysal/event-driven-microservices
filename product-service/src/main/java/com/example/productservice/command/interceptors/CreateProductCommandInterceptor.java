package com.example.productservice.command.interceptors;

import com.example.productservice.command.CreateProductCommand;
import com.example.productservice.core.data.ProductLookUpRepository;
import com.example.productservice.core.data.ProductLookupEntity;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

@Component
@AllArgsConstructor
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);
    private final ProductLookUpRepository productLookUpRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> list) {
        return (index, command) -> {

            LOGGER.info("Intercepted command: " + command.getPayloadType());

            if (CreateProductCommand.class.equals(command.getPayloadType())) {
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                ProductLookupEntity entity = productLookUpRepository.findByProductIdOrTitle(
                        createProductCommand.getProductId(), createProductCommand.getTitle());

                if (entity != null) {
                    throw new IllegalStateException(
                            "Product with productId " + createProductCommand.getProductId() + " already exists");
                }
            }

            return command;
        };
    }
}
