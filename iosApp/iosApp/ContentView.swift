import SwiftUI
import shared

struct CircleView: View {
    let circleCenters: [CircleColumn]
    let circleRadius: CGFloat
    
    var body: some View {
        let centers = circleCenters.flatMap {$0.column }.map { CircleCenter(center: CGPoint(x: $0.centerX, y: $0.centerY), color: convertStateToColor(state: $0.state), size: circleRadius * 2) }
        ZStack {
            ForEach(centers, id: \.id) { center in
                Circle()
                    .foregroundColor(center.color)
                    .frame(width: center.size, height: center.size)
                    .position(center.center)
            }
        }
    }
    
    private func convertStateToColor(state: CircleState) -> Color {
        switch state {
        case CircleState.blue:
            return Color.blue
        case CircleState.red:
            return Color.red
        case CircleState.empty:
            return Color.gray
        default:
            return Color.gray
        }
    }
}

struct MaterialButtonStyle: ButtonStyle {
    func makeBody(configuration: Self.Configuration) -> some View {
        configuration.label
            .foregroundColor(.white)
            .padding(12)
            .background(Color.purple)
            .cornerRadius(10)
            .scaleEffect(configuration.isPressed ? 0.9 : 1.0)
    }
}

struct CircleCenter: Identifiable {
    let id = UUID()
    let center: CGPoint
    let color: Color
    let size: CGFloat
}

struct ContentView: View {

    private let padding: CGFloat = 10
    private let circleRadius: CGFloat = 15
     
    @State private var modifiableField: [CircleColumn]
    @State private var initialField: [CircleColumn]
    
    private let width = UIScreen.main.bounds.width
    private let height = UIScreen.main.bounds.height
    
    @State private var isEditableField = true
    private let fieldProcessor: CircleFieldProcessor = CircleFieldProcessor()
    
    init(isEditableField: Bool = true) {
        self.isEditableField = isEditableField
        self.initialField = [CircleColumn]()
        self.modifiableField = [CircleColumn]()
        let data = initialState(processor: fieldProcessor)
        self.initialField = data
        self.modifiableField = data
    }

    var body: some View {
        
        VStack {
            let circleCenters = if (isEditableField) {modifiableField} else {initialField}
            CircleView(circleCenters: circleCenters, circleRadius: circleRadius)
                .gesture(
                    DragGesture(minimumDistance: 0)
                        .onEnded { value in
                            if (isEditableField) {
                                updateModifiedField(data: fieldProcessor.moveTappedCircle(offset: CircleOffset(x: value.location.x,y: value.location.y)))
                            }
                        }
            )

            Button(action: {
                updateModifiedField(data: fieldProcessor.moveUnhappyCircles(times: 1))
            }) {
                Text("Преобразовать").font(.headline)
            }.buttonStyle(MaterialButtonStyle())
            
            Button(action: {
                isEditableField = false
                let data = initialState(processor: fieldProcessor)
                updateInitialField(data: data)
                updateModifiedField(data: data)
            }) {
                Text("Сгенерировать заново").font(.headline)
            }.buttonStyle(MaterialButtonStyle())

            let toggleText = if(isEditableField) {"Посмотреть исходный вариант" }else {"Продолжить преобразования"}
            Toggle(toggleText, isOn: $isEditableField)
                .padding(8)
        }
    }
    
    private func initialState(processor: CircleFieldProcessor) -> Array<CircleColumn> {
        let rawWidth = width - 2 * padding
        let rawHeight = height * 0.75 - 2 * padding
        let totalCirclesX = (rawWidth / (2 * circleRadius + padding))
        let totalCirclesY = (rawHeight / (2 * circleRadius + padding))
        return processor.doInit(totalCirclesX: Int32(totalCirclesX), totalCirclesY: Int32(totalCirclesY))
    }
    
    private func updateModifiedField(data: Array<CircleColumn>) {
        self.modifiableField = data
    }
    
    private func updateInitialField(data: Array<CircleColumn>) {
        self.initialField = data
    }
}


struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
