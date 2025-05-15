module;

export module qct:common.crtp;

namespace qct::common::crtp {
/**
 * A helper template for using std::visit with a std::variant.
 * @tparam Ts the types of the variant
 */
export template <class... Ts>
struct Overloaded : Ts... {
  using Ts::operator()...;
};

/**
 * A helper template for using std::visit with a std::variant.
 * @tparam Ts the types of the variant
 */
export template <class... Ts>
Overloaded(Ts...) -> Overloaded<Ts...>;
}  // namespace qct::common::crtp