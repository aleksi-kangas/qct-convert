module;

#include <concepts>
#include <filesystem>

export module qctexport:exporter;

import qct;

namespace qct::ex {
template <class C>
class AbstractExporter;

template <typename T>
concept Exporter = requires(T t) {
  {
    t.exportTo(std::declval<const QctFile&>(), std::declval<const std::filesystem::path&>())
  } -> std::same_as<void>;
  requires std::derived_from<T, AbstractExporter<T>>;
};

template <class C>
class AbstractExporter {
 public:
  virtual ~AbstractExporter() = default;

  void exportTo(const QctFile& file, const std::filesystem::path& path) const {
    static_assert(Exporter<C>, "C must be a concrete class type that implements Exporter.");
    underlying().exportTo(file, path);
  }

private:
  friend C;

  [[nodiscard]] C& underlying() { return static_cast<C&>(*this); }
  [[nodiscard]] const C& underlying() const { return static_cast<const C&>(*this); }
};
}  // namespace qct::ex
