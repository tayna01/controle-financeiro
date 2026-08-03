export function AuthAside() {
  return (
    <aside className="hidden flex-col justify-between bg-gradient-to-br from-[#5b21b6] to-[#a21caf] p-12 text-white lg:flex lg:w-1/2">
      <div className="flex items-center gap-3 text-xl font-bold">
        Controle Financeiro
      </div>

      <div>
        <h1 className="text-4xl font-bold leading-tight">
          Organize suas finanças
          <br />
          em um só lugar
        </h1>
        <p className="mt-4 max-w-md text-white/80">
          Acompanhe receitas e despesas, veja gráficos do seu mês e mantenha o
          controle do seu dinheiro.
        </p>
      </div>

      <p className="text-sm text-white/60">versão 1.0</p>
    </aside>
  )
}
