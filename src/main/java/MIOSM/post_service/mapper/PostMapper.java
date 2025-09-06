package MIOSM.post_service.mapper;

import MIOSM.post_service.dto.PostCreateRequestDto;
import MIOSM.post_service.dto.PostResponseDto;
import MIOSM.post_service.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {LocalDateTime.class, ZoneId.class, ZonedDateTime.class, DateTimeFormatter.class}
)
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "postId", source = "postId")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "toUtc")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "toUtc")
    PostResponseDto postToPostResponseDto(Post post);

    @Mapping(target = "postId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Post postCreateRequestDtoToPost(PostCreateRequestDto dto);
    
    @Named("toUtc")
    default LocalDateTime toUtc(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(ZoneId.systemDefault())
                      .withZoneSameInstant(ZoneId.of("UTC"))
                      .toLocalDateTime();
    }
}